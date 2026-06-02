package com.example.ILovePdf.service;

import com.example.ILovePdf.config.StoragePaths;
import com.example.ILovePdf.dto.JobDetails;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PdfService {

    private final StoragePaths storagePaths;
    private final Map<UUID, JobDetails> jobs = new ConcurrentHashMap<>();

    public PdfService(StoragePaths storagePaths) {
        this.storagePaths = storagePaths;
    }

    public JobDetails merge(List<String> paths) throws IOException {
        UUID jobId = UUID.randomUUID();
        Path resultsDir = storagePaths.resultsDir();
        Files.createDirectories(resultsDir);

        Path outputPath = resultsDir.resolve(jobId + ".pdf");
        String outputFile = outputPath.toString();
        List<String> result = new ArrayList<>();
        result.add(outputFile);

        JobDetails response = new JobDetails();
        response.setJobId(jobId);
        response.setOperation("MERGE");
        response.setInputFiles(paths);
        response.setOutputFile(result);
        response.setStatus("PENDING");
        jobs.put(jobId, response);

        try {
            response.setStatus("PROCESSING");

            PDFMergerUtility merger = new PDFMergerUtility();
            for (String file : paths) {
                merger.addSource(file);
            }
            merger.setDestinationFileName(outputFile);
            merger.mergeDocuments(null);

            response.setStatus("COMPLETED");
        } catch (IOException e) {
            response.setStatus("FAILED");
            throw e;
        }

        return response;
    }

    public Optional<JobDetails> getJob(UUID jobId) {
        return Optional.ofNullable(jobs.get(jobId));
    }

    public JobDetails split(List<String> paths) throws IOException{
        if (paths.isEmpty()) {
            throw new IllegalArgumentException(
                    "No PDF provided"
            );
        }

        UUID jobId = UUID.randomUUID();

        Path resultsDir = storagePaths.resultsDir();
        Files.createDirectories(resultsDir);

        JobDetails response = new JobDetails();
        response.setJobId(jobId);
        response.setOperation("SPLIT");
        response.setInputFiles(paths);
        response.setStatus("PENDING");
        jobs.put(jobId, response);

        List<String> outputFiles = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(new File(paths.get(0)))) {
            response.setStatus("PROCESSING");

            Splitter splitter = new Splitter();
            List<PDDocument> pages = splitter.split(document);

            int pageNumber = 1;
            for (PDDocument pageDoc : pages) {
                Path outputPath = resultsDir.resolve(jobId + "_page_" + pageNumber + ".pdf");
                try (pageDoc) {
                    pageDoc.save(outputPath.toFile());
                }
                outputFiles.add(outputPath.toString());
                pageNumber++;
            }

            response.setStatus("COMPLETED");
        } catch (IOException e) {
            response.setStatus("FAILED");
            throw e;
        }

        response.setOutputFile(outputFiles);
        return response;
    }

    public JobDetails compress(List<String> paths)
            throws IOException {

        String inputFile = paths.get(0);
        UUID jobId = UUID.randomUUID();
        Path outputPath =
                storagePaths.resultsDir()
                        .resolve(jobId + ".pdf");

        JobDetails response =
                new JobDetails();

        response.setJobId(jobId);
        response.setOperation("COMPRESS");
        response.setInputFiles(paths);
        response.setStatus("PENDING");

        jobs.put(jobId, response);

        try {
            response.setStatus("PROCESSING");

            try (PDDocument document =
                         Loader.loadPDF(
                                 new File(inputFile))) {
                document.save(
                        outputPath.toFile()
                );
            }
            response.setOutputFile(
                    outputPath.toString().lines().toList()
            );
            response.setStatus("COMPLETED");
        } catch (IOException e) {
            response.setStatus("FAILED");
            throw e;
        }
        return response;
    }
}
