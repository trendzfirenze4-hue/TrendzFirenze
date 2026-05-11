package com.mydev.ecommerce.common.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Cloudinary cloudinary;

    public FileStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public UploadResult saveFile(MultipartFile file) throws IOException {
        return saveFile(file, "trendz-firenze/products");
    }

    public UploadResult saveGiftBoxFile(MultipartFile file) throws IOException {
        return saveFile(file, "trendz-firenze/gift-boxes");
    }

    public UploadResult saveBrandShowcaseFile(MultipartFile file) throws IOException {
        return saveFile(file, "trendz-firenze/brand-showcases");
    }

    public UploadResult saveFile(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String contentType = file.getContentType();

        boolean isImage = contentType != null && contentType.startsWith("image/");
        boolean isVideo =
                "video/mp4".equals(contentType) ||
                "video/webm".equals(contentType) ||
                "video/quicktime".equals(contentType);

        if (!isImage && !isVideo) {
            throw new RuntimeException("Only image or video files allowed");
        }

        String original = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "media" : file.getOriginalFilename()
        );

        String safeName = sanitize(original);
        String publicId = UUID.randomUUID() + "_" + safeName;

        String resourceType = isVideo ? "video" : "image";

        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().upload(
                file.getInputStream(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "public_id", publicId,
                        "resource_type", resourceType
                )
        );

        String fileUrl = (String) result.get("secure_url");
        String cloudinaryPublicId = (String) result.get("public_id");

        if (fileUrl == null || fileUrl.isBlank()) {
            throw new RuntimeException("Cloudinary did not return file URL");
        }

        return new UploadResult(fileUrl, cloudinaryPublicId, resourceType);
    }

    public void deleteFile(String publicId, String resourceType) {
        if (publicId == null || publicId.isBlank()) return;

        try {
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "resource_type",
                            resourceType == null || resourceType.isBlank() ? "image" : resourceType
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from Cloudinary", e);
        }
    }



public void deleteFile(String publicId) {
    if (publicId == null || publicId.isBlank()) return;

    try {
        cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap("resource_type", "image")
        );

        cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap("resource_type", "video")
        );

    } catch (Exception e) {
        throw new RuntimeException("Failed to delete file from Cloudinary", e);
    }
}




    private String sanitize(String name) {
        if (name == null || name.isBlank()) return "media";

        String cleaned = Normalizer.normalize(name, Normalizer.Form.NFKC);
        cleaned = cleaned.replaceAll("[\\r\\n]", "");
        cleaned = cleaned.replaceAll("[^a-zA-Z0-9._-]", "_");

        return cleaned.isBlank() ? "media" : cleaned;
    }

    public record UploadResult(String imageUrl, String publicId, String resourceType) {}
}