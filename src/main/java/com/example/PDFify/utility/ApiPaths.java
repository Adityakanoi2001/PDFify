package com.example.PDFify.utility;

public interface ApiPaths {
String BASE_PATH = Constants.CONTEXT_PATH + "/pdfify-Controller";
String SINGLE_FILE_UPLOAD = "/singleFileUpload";
String GET_FIRST_FILE_WHICH_CONTAINS_SEARCHED_TEXT = "/firstFileForSearchedText";
String GET_AI_GENERATED_SUMMARY_OF_UPLOADED_FILE = "/getSummary";
String MULTIPLE_FILE_UPLOAD = "/uploadMultipleFiles";
}
