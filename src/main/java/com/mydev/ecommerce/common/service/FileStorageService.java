


// package com.mydev.ecommerce.common.service;

// import com.cloudinary.Cloudinary;
// import com.cloudinary.utils.ObjectUtils;
// import org.springframework.stereotype.Service;
// import org.springframework.util.StringUtils;
// import org.springframework.web.multipart.MultipartFile;

// import java.io.IOException;
// import java.text.Normalizer;
// import java.util.Map;
// import java.util.UUID;

// @Service
// public class FileStorageService {

//     private final Cloudinary cloudinary;

//     public FileStorageService(Cloudinary cloudinary) {
//         this.cloudinary = cloudinary;
//     }

//     public UploadResult saveFile(MultipartFile file) throws IOException {
//         if (file == null || file.isEmpty()) {
//             throw new RuntimeException("File is empty");
//         }

//         String contentType = file.getContentType();
//         if (contentType == null || !contentType.startsWith("image/")) {
//             throw new RuntimeException("Only image files are allowed");
//         }

//         String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "image" : file.getOriginalFilename());
//         String safeName = sanitize(original);
//         String publicId = UUID.randomUUID() + "_" + safeName;

//         @SuppressWarnings("unchecked")
//         Map<String, Object> result = cloudinary.uploader().upload(
//                 file.getBytes(),
//                 ObjectUtils.asMap(
//                         "folder", "trendz-firenze/products",
//                         "public_id", publicId,
//                         "resource_type", "image"
//                 )
//         );

//         String imageUrl = (String) result.get("secure_url");
//         String cloudinaryPublicId = (String) result.get("public_id");

//         if (imageUrl == null || imageUrl.isBlank()) {
//             throw new RuntimeException("Cloudinary did not return image URL");
//         }

//         return new UploadResult(imageUrl, cloudinaryPublicId);
//     }

//     public void deleteFile(String publicId) {
//         if (publicId == null || publicId.isBlank()) {
//             return;
//         }

//         try {
//             cloudinary.uploader().destroy(
//                     publicId,
//                     ObjectUtils.asMap("resource_type", "image")
//             );
//         } catch (Exception e) {
//             throw new RuntimeException("Failed to delete image from Cloudinary", e);
//         }
//     }

//     private String sanitize(String name) {
//         if (name == null || name.isBlank()) {
//             return "image";
//         }

//         String cleaned = Normalizer.normalize(name, Normalizer.Form.NFKC);
//         cleaned = cleaned.replaceAll("[\\r\\n]", "");
//         cleaned = cleaned.replaceAll("[^a-zA-Z0-9._-]", "_");

//         if (cleaned.isBlank()) {
//             return "image";
//         }

//         return cleaned;
//     }

//     public record UploadResult(String imageUrl, String publicId) {}
// }








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

    /**
     * ✅ EXISTING METHOD (UNCHANGED)
     * Used for PRODUCT uploads
     */
    public UploadResult saveFile(MultipartFile file) throws IOException {
        return saveFile(file, "trendz-firenze/products");
    }

    /**
     * 🔥 NEW METHOD (FOR GIFT BOX)
     */
    public UploadResult saveGiftBoxFile(MultipartFile file) throws IOException {
        return saveFile(file, "trendz-firenze/gift-boxes");
    }

    /**
     * 🔥 CORE COMMON METHOD (DO NOT CALL DIRECTLY FROM CONTROLLER)
     */
    public UploadResult saveFile(MultipartFile file, String folder) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Only image files are allowed");
        }

        String original = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "image" : file.getOriginalFilename()
        );

        String safeName = sanitize(original);
        String publicId = UUID.randomUUID() + "_" + safeName;

        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "public_id", publicId,
                        "resource_type", "image"
                )
        );

        String imageUrl = (String) result.get("secure_url");
        String cloudinaryPublicId = (String) result.get("public_id");

        if (imageUrl == null || imageUrl.isBlank()) {
            throw new RuntimeException("Cloudinary did not return image URL");
        }

        return new UploadResult(imageUrl, cloudinaryPublicId);
    }

    public void deleteFile(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }

        try {
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap("resource_type", "image")
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image from Cloudinary", e);
        }
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

    public record UploadResult(String imageUrl, String publicId) {}
}