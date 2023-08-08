package com.example.PDFify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
  private String fileName;
  private String fileDownloadUri;
  private String fileType;
  private long size;
}
