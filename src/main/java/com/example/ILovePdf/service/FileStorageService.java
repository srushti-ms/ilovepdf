package com.example.ILovePdf.service;

import com.example.ILovePdf.config.StoragePaths;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final StoragePaths storagePaths;

    public FileStorageService(StoragePaths storagePaths) {
        this.storagePaths = storagePaths;
    }

    public List<String> save(List<MultipartFile> files) throws IOException {
        Path uploadDir = storagePaths.uploadDir();
        Files.createDirectories(uploadDir);

        List<String> paths = new ArrayList<>();
        for (MultipartFile file : files) {
            Path destination = uploadDir.resolve(
                    UUID.randomUUID() + "_" + file.getOriginalFilename()
            );
            file.transferTo(destination);
            paths.add(destination.toString());
        }
        return paths;
    }
}
