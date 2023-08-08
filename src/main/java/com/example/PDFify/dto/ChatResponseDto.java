package com.example.PDFify.dto;

import java.util.List;

import org.apache.logging.log4j.message.Message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponseDto {

  private List<Choice> choices;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Choice {

    private int index;
    private MessageDto message;
  }
}

