package ru.gigafood.backend.tool;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

@Component
public class FileTools {

    public String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    public boolean deleteFile(Path path) {
        return path.toFile().delete();
    }

    // другие методы...
}
