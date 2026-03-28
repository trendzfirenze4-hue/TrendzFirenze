// package com.mydev.ecommerce.config;

// import com.mydev.ecommerce.common.constants.FileConstants;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.*;

// @Configuration
// public class FileStorageConfig implements WebMvcConfigurer {

//     @Override
//     public void addResourceHandlers(ResourceHandlerRegistry registry) {

//         registry
//                 .addResourceHandler("/images/**")
//                 .addResourceLocations("file:" + FileConstants.PRODUCT_UPLOAD_DIR);
//     }
// }






package com.mydev.ecommerce.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(FileStorageProperties.class)
public class FileStorageConfig implements WebMvcConfigurer {

    private final FileStorageProperties fileStorageProperties;

    public FileStorageConfig(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadDir = fileStorageProperties.getProductUploadDir();
        String normalizedPath = normalizeForResourceLocation(uploadDir);

        System.out.println("RAW PRODUCT_UPLOAD_DIR = " + uploadDir);
        System.out.println("NORMALIZED RESOURCE PATH = " + normalizedPath);
        System.out.println("FINAL RESOURCE LOCATION = file:" + normalizedPath);

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + normalizedPath);
    }

    private String normalizeForResourceLocation(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalStateException("app.file.product-upload-dir is not configured");
        }

        String normalized = path.replace("\\", "/");
        if (!normalized.endsWith("/")) {
            normalized += "/";
        }
        return normalized;
    }
}