package com.example.PDFify.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import com.example.PDFify.properties.ApplicationProperties;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
  @Autowired
  ApplicationProperties applicationProperties;

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
        .paths(PathSelectors.any())
        .build()
        .apiInfo(getApiInfo());
  }

  private ApiInfo getApiInfo() {
    return new ApiInfoBuilder().title("PDFify Central Swagger")
        .version(applicationProperties.getVersion())
        .description("API for Amazing Performing API Operations")
        .contact(new Contact("Aditya Kanoi", "https://adityakanoi123.wixsite.com/adityakanoi", "adityakanoi123@gmail.com"))
        .license("Apache License Version 2.0")
        .build();
  }
}
