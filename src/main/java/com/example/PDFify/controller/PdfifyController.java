package com.example.PDFify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.example.PDFify.dto.FileContentDto;
import com.example.PDFify.dto.FileUploadResponse;
import com.example.PDFify.service.serviceInterface.PdfExtractorServiceInterface;
import com.example.PDFify.utility.ApiPaths;
import com.example.PDFify.utility.HelperFunctions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(ApiPaths.BASE_PATH)
public class PdfifyController {

  @Autowired
  PdfExtractorServiceInterface pdfExtractorServiceInterface;
  @Autowired
  HelperFunctions helperFunctions;

  @PostMapping(ApiPaths.SINGLE_FILE_UPLOAD)
  public ResponseEntity<FileUploadResponse> singleFileUpload(@RequestParam("file") MultipartFile file) {
    try {
      if (file.isEmpty() || !file.getContentType().equals("application/pdf")) {
        return ResponseEntity.badRequest().build();
      }

      String fileName = pdfExtractorServiceInterface.storeSingleFile(file);

      log.info("User Uploaded a file with FileName: {}", fileName);

      String fileDownloadUri =
          ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/").path(fileName).toUriString();

      FileUploadResponse response =
          new FileUploadResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
      return ResponseEntity.ok(response);
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @GetMapping(ApiPaths.GET_FIRST_FILE_WHICH_CONTAINS_SEARCHED_TEXT)
  public ResponseEntity<Resource> getFirstFileWhichContainsText(@RequestParam String searchText) {
    try {
      Resource resource = pdfExtractorServiceInterface.firstFileResult(searchText);
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
          .body(resource);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping(ApiPaths.GET_AI_GENERATED_SUMMARY_OF_UPLOADED_FILE)
  public ResponseEntity<FileContentDto> summarizeFile(@RequestParam("file") MultipartFile file) {
    try {
      FileContentDto fileContentDto = pdfExtractorServiceInterface.summaryFileResult(file);
      return new ResponseEntity<>(fileContentDto, HttpStatus.OK);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping(ApiPaths.MULTIPLE_FILE_UPLOAD)
  public List<ResponseEntity<FileUploadResponse>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
    List<ResponseEntity<FileUploadResponse>> responses = new ArrayList<>();

    for (MultipartFile file : files) {
      try {
        ResponseEntity<FileUploadResponse> response = singleFileUpload(file);
        responses.add(response);
      } catch (Exception e) {
        ResponseEntity<FileUploadResponse> errorResponse = helperFunctions.handleFileUploadException(e);
        responses.add(errorResponse);
      }
    }
    return responses;
  }
}
