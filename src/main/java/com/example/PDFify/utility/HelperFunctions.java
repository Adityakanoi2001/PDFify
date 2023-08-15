package com.example.PDFify.utility;

import java.beans.JavaBean;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.PDFify.dto.FileUploadResponse;

@Service
public class HelperFunctions {

  /**
   * Generates a custom error response for file upload exceptions.
   *
   * @param e The exception that occurred during file upload.
   * @return A ResponseEntity with a custom error response.
   */
  public ResponseEntity<FileUploadResponse> handleFileUploadException(Exception e) {
    // Handle file upload exceptions and create a custom error response
    FileUploadResponse errorResponse = new FileUploadResponse("An error occurred during File upload.", null, null, 0);
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    return new ResponseEntity<>(errorResponse, status);
  }
}
