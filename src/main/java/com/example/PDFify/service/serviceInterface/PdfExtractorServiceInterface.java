package com.example.PDFify.service.serviceInterface;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import com.example.PDFify.dto.FileContentDto;

public interface PdfExtractorServiceInterface {

  /**
   * @param file
   * @return
   * @throws Exception
   */
  public String storeSingleFile(MultipartFile file) throws Exception;

  /**
   * @param searchString
   * @return
   * @throws Exception
   */
  public Resource firstFileResult(String searchString) throws Exception;

  /**
   * @param multipartFile
   * @return
   */
  public FileContentDto summaryFileResult(MultipartFile multipartFile);

}
