package com.example.instagramclone;

import com.example.instagramclone.config.DotenvInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InstagramCloneTemplateApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(InstagramCloneTemplateApplication.class);
        app.addInitializers(new DotenvInitializer());
        app.run(args);
    }

}
