package com.example.PDFify.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.PDFify.dto.FileUploadResponse;
import com.example.PDFify.service.PdfExtractorService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/api/v1/pdfify")
@RequiredArgsConstructor
@Validated
public class PdfifyController {

  @Autowired
  PdfExtractorService pdfExtractorService;

  @Operation(description = "API ENDPOINT FOR UPLOADING A SINGLE PDF FILE")
  @PostMapping("/storeSingleFile")
  public FileUploadResponse uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
    String fileName = pdfExtractorService.storeSingleFile(file);
    log.info("Invoking API to Start Saving The File in Server and Database");
    String fileDownloadUri =
        ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/").path(fileName).toUriString();

    return new FileUploadResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
  }

  @Operation(description = "API ENDPOINT FOR DOWNLOADING A SINGLE PDF FILE")
  @GetMapping("/downloadFile/{fileName:.+}")
  public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request)
      throws Exception {
    log.info("Invoking API to start Download of the Mentioned File with File Name {}", fileName);
    Resource resource = pdfExtractorService.downloadSinglePDFFile(fileName);

    // Try to determine file's content type
    String contentType = null;
    try {
      contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
    } catch (IOException ex) {
      log.info("Could not determine file type.");
    }

    // Fallback to the default content type if type could not be determined
    if (contentType == null) {
      contentType = "application/octet-stream";
    }
    return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource);
  }

  @Operation(description = "API ENDPOINT FOR SEARCHING A TEXT IN ALL THE FILES AND GETTING LIST OF FILE NAMES")
  @GetMapping("/searchTextInFile")
  public ResponseEntity<List<String>> searchedFiles(@RequestParam String searchText) {
    log.info("Invoking API to Start Search In MongoDB and get List of Files which Contains the Searched Text {} ",
        searchText);
    pdfExtractorService.findMatchingTextInContent(searchText);
    List<String> searchedFilesNames = pdfExtractorService.findMatchingTextInContent(searchText);
    return new ResponseEntity<>(searchedFilesNames, HttpStatus.OK);
  }

  @Operation(description = "API ENDPOINT FOR GETTING THE FIRST FILE WHERE SEARCHED TEXT EXISTS")
  @GetMapping("/getFileWhichContainsSearchText")
  public ResponseEntity<Resource> result(@RequestParam String searchText) throws Exception {
    log.info("Invoking API to Get Results");
    Resource resource = pdfExtractorService.firstFileResult(searchText);
    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        .body(resource);
  }

  @Operation(description = "API ENDPOINT FOR SUMMARIZING A PDF FILE and Saving In Database")
  @PostMapping("/summarizeMyFile")
  public ResponseEntity<String> summarizeFile(@RequestParam("file") MultipartFile file) throws Exception {
    log.info("Invoking API to Start a Summary of Your Document File");
    String summary = pdfExtractorService.summaryFileResult(file);
    return new ResponseEntity<>(summary, HttpStatus.OK);
  }

}
