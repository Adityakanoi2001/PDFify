package com.example.PDFify.entity;

import java.io.Serializable;
import java.net.URL;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "PDFify")
public class PDFEntity implements Serializable {
  private static final long serialVersionUID = -3415831946992984528L;
  private String fileName;
  private String content;
  private long fileSize;
  private String summary;
  private URL filePath;
}
