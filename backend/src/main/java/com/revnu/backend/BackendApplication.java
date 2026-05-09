package com.revnu.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@EnableCaching
public class BackendApplication {

    public static void main(String[] args) {
        loadDotenv(".");
        loadDotenv("..");
        SpringApplication.run(BackendApplication.class, args);
    }

    private static void loadDotenv(String directory) {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory(directory)
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        } catch (Exception ignored) {

        }
    }

}
