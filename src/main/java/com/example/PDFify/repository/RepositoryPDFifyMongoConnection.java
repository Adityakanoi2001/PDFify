package com.example.PDFify.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.PDFify.entity.PDFEntity;

public interface RepositoryPDFifyMongoConnection extends MongoRepository<PDFEntity, String> {
}
