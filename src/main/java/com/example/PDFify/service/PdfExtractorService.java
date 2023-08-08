package com.example.PDFify.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.PDFify.configuration.FileStorageProperties;
import com.example.PDFify.dto.ChatRequestDto;
import com.example.PDFify.dto.ChatResponseDto;
import com.example.PDFify.dto.FileContentDto;
import com.example.PDFify.dto.MessageDto;
import com.example.PDFify.entity.PDFEntity;
import com.example.PDFify.entity.PDFifyChatGPTEntity;
import com.example.PDFify.repository.RepositoryMongoCustom;
import com.example.PDFify.repository.RepositoryPDFifyChatGPTMongoConnection;
import com.example.PDFify.repository.RepositoryPDFifyMongoConnection;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PdfExtractorService {

  @Autowired
  RepositoryPDFifyMongoConnection repositoryPDFifyMongoConnection;
  @Autowired
  RepositoryPDFifyChatGPTMongoConnection repositoryPDFifyChatGPTMongoConnection;
  @Autowired
  RepositoryMongoCustom repositoryMongoCustom;
  private final Path fileStorageLocation;
  @Autowired
  private RestTemplate restTemplate;

  @Value("${openai.model}")
  private String model;

  @Value("${openai.max-completions}")
  private int maxCompletions;

  @Value("${openai.temperature}")
  private double temperature;

  @Value("${openai.max_tokens}")
  private int maxTokens;

  @Value("${openai.api.url}")
  private String apiUrl;

  @Autowired
  public PdfExtractorService(FileStorageProperties fileStorageProperties) throws Exception {
    this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

    try {
      Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
      throw new Exception("Could not create the directory where the uploaded files will be stored.", ex);
    }
  }

  public String storeSingleFile(MultipartFile file) throws Exception {
    // Normalize file name
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    try {
      if (fileName.contains("..")) {
        throw new Exception("Sorry! Filename contains invalid path sequence " + fileName);
      }
      // Copy file to the target location (Replacing existing file with the same name)
      Path targetLocation = this.fileStorageLocation.resolve(fileName);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
      extractContentFromUploadedFile(file);
      return fileName;
    } catch (Exception ex) {
      throw new Exception("Could not store file " + fileName + ". Please try again!", ex);
    }
  }

  private FileContentDto extractContentFromUploadedFile(final MultipartFile multipartFile) {
    log.warn("Invoking Function with {} File For Data Scan", multipartFile.getOriginalFilename());
    FileContentDto fileContentDto = new FileContentDto();
    PDFEntity pdfEntity = new PDFEntity();
    try (final PDDocument document = PDDocument.load(multipartFile.getInputStream())) {
      final PDFTextStripper pdfStripper = new PDFTextStripper();
      String text = pdfStripper.getText(document);
      fileContentDto.setContent(text.replaceAll("\\s+", " ").toLowerCase().trim());
      fileContentDto.setFileName(multipartFile.getOriginalFilename());
      fileContentDto.setFileSize(multipartFile.getSize());
      BeanUtils.copyProperties(fileContentDto, pdfEntity);
      repositoryPDFifyMongoConnection.save(pdfEntity);
    } catch (final Exception ex) {
      log.error("Error parsing PDF", ex);
    }
    return fileContentDto;
  }

  public Resource downloadSinglePDFFile(String fileName) throws Exception {
    try {
      Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.exists()) {
        return resource;
      } else {
        throw new Exception("File not found " + fileName);
      }
    } catch (MalformedURLException ex) {
      throw new Exception("File not found " + fileName, ex);
    }
  }

  public List<String> findMatchingTextInContent(String searchString) {
    List<PDFEntity> pdfEntityList = repositoryMongoCustom.findMatchingTextInContent(searchString);
    return pdfEntityList.stream().map(PDFEntity::getFileName).collect(Collectors.toList());
  }

  public Resource firstFileResult(String searchString) throws Exception {
    try {
      searchString = findMatchingTextInContent(searchString).get(0);
      Path filePath = this.fileStorageLocation.resolve(searchString).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if (resource.exists()) {
        return resource;
      } else {
        throw new Exception("File not found " + searchString);
      }
    } catch (MalformedURLException ex) {
      throw new Exception("File not found " + searchString, ex);
    }
  }

  public String summaryFileResult(MultipartFile multipartFile) {
    log.warn("Invoking Function with {} File For Data Scan", multipartFile.getOriginalFilename());
    FileContentDto fileContentDto = new FileContentDto();
    String summary;
    PDFifyChatGPTEntity pdFifyChatGPTEntity = new PDFifyChatGPTEntity();
    try (final PDDocument document = PDDocument.load(multipartFile.getInputStream())) {

      final PDFTextStripper pdfStripper = new PDFTextStripper();
      String text = pdfStripper.getText(document);

      fileContentDto.setContent(text.replaceAll("\\s+", " ").toLowerCase().trim());
      fileContentDto.setFileName(multipartFile.getOriginalFilename());
      fileContentDto.setFileSize(multipartFile.getSize());

      String prompt = text + "              "
          + "Please Summarize these Lines Written above and give a small and quick Summary about 1 Paragraph for the text above";

      ChatRequestDto request =
          new ChatRequestDto(model, List.of(new MessageDto("user", prompt)), maxCompletions, temperature, maxTokens);
      ChatResponseDto response = restTemplate.postForObject(apiUrl, request, ChatResponseDto.class);
      summary = response.getChoices().get(0).getMessage().getContent();

      BeanUtils.copyProperties(fileContentDto, pdFifyChatGPTEntity);
      pdFifyChatGPTEntity.setSummary(summary);
      repositoryPDFifyChatGPTMongoConnection.save(pdFifyChatGPTEntity);

    } catch (IOException e) {
      log.error("Error parsing PDF", e);
      throw new RuntimeException(e);
    }
    return summary;
  }
}
