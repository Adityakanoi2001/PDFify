package com.example.PDFify.entity;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "PDFifyChatGPT")
public class PDFifyChatGPTEntity implements Serializable{
  private static final long serialVersionUID = 8020540050108990536L;
    private String fileName;
    private String content;
    private long fileSize;
    private String summary;

}
