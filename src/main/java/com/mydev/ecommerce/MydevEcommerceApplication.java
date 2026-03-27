package com.mydev.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.mydev.ecommerce")
public class MydevEcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MydevEcommerceApplication.class, args);
    }

}