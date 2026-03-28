// package com.mydev.ecommerce.common.service;

// import com.mydev.ecommerce.common.constants.FileConstants;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.IOException;
// import java.nio.file.*;
// import java.util.UUID;

// @Service
// public class FileStorageService {

//     public String saveFile(MultipartFile file) throws IOException {

//         String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

//         Path path = Paths.get(FileConstants.PRODUCT_UPLOAD_DIR + filename);

//         Files.createDirectories(path.getParent());

//         Files.write(path, file.getBytes());

//         return FileConstants.PRODUCT_IMAGE_URL + filename;
//     }
// }


package com.mydev.ecommerce.common.service;

import com.mydev.ecommerce.config.FileStorageProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadPath;

    public FileStorageService(FileStorageProperties properties) {
        String uploadDir = properties.getProductUploadDir();

        if (uploadDir == null || uploadDir.isBlank()) {
            throw new RuntimeException("Upload directory not configured");
        }

        System.out.println("RAW PRODUCT_UPLOAD_DIR = " + uploadDir);

        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        System.out.println("ABSOLUTE UPLOAD PATH = " + this.uploadPath);

        try {
            Files.createDirectories(uploadPath);
            System.out.println("UPLOAD DIRECTORY EXISTS = " + Files.exists(uploadPath));
            System.out.println("UPLOAD DIRECTORY READABLE = " + Files.isReadable(uploadPath));
            System.out.println("UPLOAD DIRECTORY WRITABLE = " + Files.isWritable(uploadPath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder: " + uploadPath, e);
        }

        System.out.println("UPLOAD FOLDER READY = " + uploadPath);
    }

    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String safeName = sanitize(original);
        String fileName = UUID.randomUUID() + "_" + safeName;

        Path target = uploadPath.resolve(fileName).normalize();

        System.out.println("ORIGINAL FILE NAME = " + original);
        System.out.println("SAFE FILE NAME = " + safeName);
        System.out.println("FINAL FILE NAME = " + fileName);
        System.out.println("TARGET FILE PATH = " + target);

        if (!target.startsWith(uploadPath)) {
            throw new RuntimeException("Invalid file path");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("FILE EXISTS AFTER SAVE = " + Files.exists(target));
        System.out.println("FILE READABLE AFTER SAVE = " + Files.isReadable(target));
        System.out.println("RETURN URL = /images/" + fileName);

        return "/images/" + fileName;
    }

    private String sanitize(String name) {
        if (name == null || name.isBlank()) {
            return "image";
        }

        String cleaned = Normalizer.normalize(name, Normalizer.Form.NFKC);
        cleaned = cleaned.replaceAll("[\\r\\n]", "");
        cleaned = cleaned.replaceAll("[^a-zA-Z0-9._-]", "_");

        if (cleaned.isBlank()) {
            return "image";
        }

        return cleaned;
    }
}