package com.example.ILovePdf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class StoragePaths {

    private final Path uploadDir;
    private final Path resultsDir;

    public StoragePaths(@Value("${app.storage.base-dir}") String baseDir) throws IOException {
        Path base = Path.of(baseDir).toAbsolutePath().normalize();
        this.uploadDir = base.resolve("uploads");
        this.resultsDir = base.resolve("results");

        Files.createDirectories(uploadDir);
        Files.createDirectories(resultsDir);
    }

    public Path uploadDir() {
        return uploadDir;
    }

    public Path resultsDir() {
        return resultsDir;
    }
}
