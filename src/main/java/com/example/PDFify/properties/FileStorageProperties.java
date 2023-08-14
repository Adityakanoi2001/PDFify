package com.example.PDFify.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
  private String uploadDir;
}
