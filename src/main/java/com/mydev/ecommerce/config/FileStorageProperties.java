package com.mydev.ecommerce.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.file")
public class FileStorageProperties {

    private String productUploadDir;

    public String getProductUploadDir() {
        return productUploadDir;
    }

    public void setProductUploadDir(String productUploadDir) {
        this.productUploadDir = productUploadDir;
    }
}