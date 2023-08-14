package com.example.PDFify.dto;

import java.net.URL;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// This DTO Corresponds to PDFEntity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileContentDto {
  private String fileName;
  private String content;
  private long fileSize;
  private String summary;
  private URL filePath;
}
