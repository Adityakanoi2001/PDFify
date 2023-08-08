package com.example.PDFify.dto;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDto {

  private String model;
  private List<MessageDto> messages;
  private int n;
  private double temperature;
  private int max_tokens;
}
