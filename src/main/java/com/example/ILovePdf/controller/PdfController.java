package com.example.ILovePdf.controller;

import com.example.ILovePdf.dto.JobDetails;
import com.example.ILovePdf.service.FileStorageService;
import com.example.ILovePdf.service.PdfService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.util.List;
import java.util.UUID;

@RequestMapping("/ilovepdf")
@RestController
public class PdfController {

    private final FileStorageService fileStorageService;
    private final PdfService pdfService;

    public PdfController(FileStorageService fileStorageService, PdfService pdfService) {
        this.fileStorageService = fileStorageService;
        this.pdfService = pdfService;
    }

    @PostMapping(value = "/jobs/merge", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JobDetails> merge(@RequestParam("files") List<MultipartFile> inputFiles)
            throws IOException {
        if (inputFiles == null || inputFiles.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<String> filePaths = fileStorageService.save(inputFiles);
        JobDetails response = pdfService.merge(filePaths);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<JobDetails> getJob(@PathVariable UUID jobId) {
        return pdfService.getJob(jobId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/jobs/split", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JobDetails> splitPdf(@RequestParam("files") List<MultipartFile> files)
            throws IOException {
        if (files == null || files.size() != 1) {
            return ResponseEntity.badRequest().build();
        }

        List<String> paths = fileStorageService.save(files);
        JobDetails response = pdfService.split(paths);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/jobs/compress", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JobDetails> compressPdf(@RequestParam("files") List<MultipartFile> files)
            throws IOException {
        if (files == null || files.size() != 1) {
            return ResponseEntity.badRequest().build();
        }

        List<String> paths = fileStorageService.save(files);
        JobDetails response = pdfService.compress(paths);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
