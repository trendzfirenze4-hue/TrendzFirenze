package com.mydev.ecommerce.config;

import com.mydev.ecommerce.common.constants.FileConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class FileStorageConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry
                .addResourceHandler("/images/**")
                .addResourceLocations("file:" + FileConstants.PRODUCT_UPLOAD_DIR);
    }
}