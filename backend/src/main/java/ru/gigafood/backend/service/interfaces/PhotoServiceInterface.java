package ru.gigafood.backend.service.interfaces;

import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import ru.gigafood.backend.entity.Photo;

public interface PhotoServiceInterface {

  /**
   * Загрузить новый файл
   *
   * @param file
   * @param user
   * @throws IOException
   */
  Photo addAttachment(MultipartFile file) throws IOException;


  /**
   * Найти Вложение по его ID
   *
   * @param attachId
   * @return
   */
  Photo findAttachById(Long attachId);	

  /**
   * Скачать файл
   *
   * @param uploadYear
   * @param fileName
   * @return
   * @throws MalformedURLException
   */
  Resource loadFileAsResource(String fileName) throws MalformedURLException;

}