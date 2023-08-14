package com.example.PDFify.repository;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.example.PDFify.entity.PDFEntity;

@Service
public class RepositoryPDFifyMongoCustom {

  @Autowired
  MongoTemplate mongoTemplate;

  public List<PDFEntity> findMatchingTextInContent(String searchString) {
    String regexPattern = "(?i).*" + searchString + ".*";
    Query query = new Query(Criteria.where("content").regex(regexPattern));
    List<PDFEntity> matchingDocuments = mongoTemplate.find(query, PDFEntity.class);
    return matchingDocuments;
  }


}
