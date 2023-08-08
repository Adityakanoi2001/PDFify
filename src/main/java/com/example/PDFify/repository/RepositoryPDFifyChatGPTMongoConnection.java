package com.example.PDFify.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.PDFify.entity.PDFifyChatGPTEntity;

public interface RepositoryPDFifyChatGPTMongoConnection extends MongoRepository<PDFifyChatGPTEntity,String> {
}
