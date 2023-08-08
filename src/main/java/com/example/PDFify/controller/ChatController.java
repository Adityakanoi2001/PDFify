package com.example.PDFify.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.PDFify.dto.ChatRequestDto;
import com.example.PDFify.dto.ChatResponseDto;
import com.example.PDFify.dto.MessageDto;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/chatGPT")
public class ChatController {

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

  @Operation(description = "API ENDPOINT FOR GETTING RESPONSE FROM CHATGPT")
  @PostMapping("/communication")
  public ChatResponseDto chatGPT(@RequestParam("prompt") String prompt) {

    ChatRequestDto request = new ChatRequestDto(model,
        List.of(new MessageDto("user", prompt)),
        maxCompletions,
        temperature,
        maxTokens);

    ChatResponseDto response = restTemplate.postForObject(apiUrl, request, ChatResponseDto.class);
    return response;
  }
}
