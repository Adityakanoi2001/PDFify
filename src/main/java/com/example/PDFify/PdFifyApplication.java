package com.example.PDFify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

import com.example.PDFify.configuration.FileStorageProperties;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
@EnableCaching
public class PdFifyApplication {

  public static void main(String[] args) {
    SpringApplication.run(PdFifyApplication.class, args);
  }

}
