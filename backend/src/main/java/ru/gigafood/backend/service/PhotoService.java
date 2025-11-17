package ru.gigafood.backend.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import ru.gigafood.backend.config.properties.AppProperties;
import ru.gigafood.backend.entity.Photo;
import ru.gigafood.backend.entity.User;
import ru.gigafood.backend.repository.PhotoRepository;
import ru.gigafood.backend.tool.FileTools;

@Service
@RequiredArgsConstructor
public class PhotoService {

  @Autowired
  private PhotoRepository attachmentRepository;

  @Autowired
  private AppProperties appProperties;

  @Autowired
  private FileTools fileTools;

  /**
   * Загрузить новый файл
   *
   * @param file
   * @param user
   * @throws IOException
   */
  public Photo addAttachment(MultipartFile file, User user) throws IOException {

    File uploadDir = new File(appProperties.getUploadPath());

    if (!uploadDir.exists()) {
      uploadDir.mkdirs();
    }
    String curDate = LocalDateTime.now().toString();

    String fileName =
        "attach_" + curDate + "_" + file.getOriginalFilename().toLowerCase().replaceAll(" ", "-");

    File dest = new File(uploadDir, fileName);

    System.out.println("Upload directory exists: " + uploadDir.exists());
    System.out.println("Upload directory absolute path: " + uploadDir.getAbsolutePath());
    System.out.println("Destination file path: " + dest.getAbsolutePath());
    System.out.println("Destination parent exists: " + dest.getParentFile().exists());
    System.out.println("File size: " + file.getSize());

    Path destPath = Paths.get(uploadDir.getAbsolutePath(), fileName);
    Files.copy(file.getInputStream(), destPath, StandardCopyOption.REPLACE_EXISTING);

    Photo attachment = Photo.builder()
        .attachTitle(fileName)
        .uploadDate(LocalDate.now())
        .extension(fileTools.getFileExtension(file.getOriginalFilename()))
        .downloadLink("/attachments/get/" + Year.now() + "/" + fileName)
        .user(user)
        .build();
    attachmentRepository.save(attachment);
    return attachment;
  }


  /**
   * Найти Вложение по его ID
   *
   * @param attachId
   * @return
   */
  public Photo findAttachById(Long attachId) throws RuntimeException {
    return attachmentRepository.findById(attachId).orElseThrow(() -> new RuntimeException("Attachment not found!"));
  }


  /**
   * Скачать файл
   *
   * @param fileName
   * @return
   * @throws MalformedURLException
   */
  public Resource loadFileAsResource(String fileName) throws MalformedURLException {
      Path fileStorageLocation =
          Paths.get(appProperties.getUploadPath()).toAbsolutePath().normalize();
      Path filePath = fileStorageLocation.resolve(fileName).normalize();
      return new UrlResource(filePath.toUri());
  }

}
