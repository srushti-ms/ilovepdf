package com.example.ILovePdf.service;

import com.example.ILovePdf.config.StoragePaths;
import com.example.ILovePdf.dto.JobDetails;
import org.springframework.stereotype.Service;
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
    private final PdfJobProcessor pdfJobProcessor;
    private final Map<UUID, JobDetails> jobs = new ConcurrentHashMap<>();

    public PdfService(StoragePaths storagePaths, PdfJobProcessor pdfJobProcessor) {
        this.storagePaths = storagePaths;
        this.pdfJobProcessor = pdfJobProcessor;
    }

    public JobDetails merge(List<String> paths) throws IOException {
        UUID jobId = UUID.randomUUID();
        Path resultsDir = storagePaths.resultsDir();
        Files.createDirectories(resultsDir);

        String outputFile = resultsDir.resolve(jobId + ".pdf").toString();

        JobDetails response = new JobDetails();
        response.setJobId(jobId);
        response.setOperation("MERGE");
        response.setInputFiles(paths);
        response.setOutputFile(List.of(outputFile));
        response.setStatus("PENDING");
        jobs.put(jobId, response);

        pdfJobProcessor.processMerge(
                response,
                paths,
                outputFile
        );

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

        pdfJobProcessor.processSplit(response, paths, outputFiles, resultsDir);

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

        pdfJobProcessor.processCompress(response, inputFile, outputPath);
        return response;
    }
}
