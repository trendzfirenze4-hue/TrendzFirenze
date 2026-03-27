package com.mydev.ecommerce.common.service;

import com.mydev.ecommerce.common.constants.FileConstants;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    public String saveFile(MultipartFile file) throws IOException {

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        Path path = Paths.get(FileConstants.PRODUCT_UPLOAD_DIR + filename);

        Files.createDirectories(path.getParent());

        Files.write(path, file.getBytes());

        return FileConstants.PRODUCT_IMAGE_URL + filename;
    }
}