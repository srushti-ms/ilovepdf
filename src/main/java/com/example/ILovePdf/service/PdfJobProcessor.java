package com.example.ILovePdf.service;

import com.example.ILovePdf.dto.JobDetails;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class PdfJobProcessor {

    @Async
    public void processMerge(JobDetails job, List<String> paths, String outputFile) throws FileNotFoundException {
        try {

            job.setStatus("PROCESSING");

            PDFMergerUtility merger =
                    new PDFMergerUtility();

            for (String file : paths) {
                merger.addSource(file);
            }

            merger.setDestinationFileName(outputFile);

            merger.mergeDocuments(null);

            job.setStatus("COMPLETED");

        } catch (Exception e) {

            job.setStatus("FAILED");
        }

    }

    @Async
    public void processCompress(JobDetails job, String inputFile, Path outputPath) throws IOException{
        try {
            job.setStatus("PROCESSING");

            try (PDDocument document =
                         Loader.loadPDF(
                                 new File(inputFile))) {
                document.save(
                        outputPath.toFile()
                );
            }
            job.setOutputFile(
                    outputPath.toString().lines().toList()
            );
            job.setStatus("COMPLETED");
        } catch (IOException e) {
            job.setStatus("FAILED");
            throw e;
        }
    }

    @Async
    public void processSplit(JobDetails job, List<String> paths,List<String> outputFiles, Path resultsDir) throws IOException{
        try (PDDocument document = Loader.loadPDF(new File(paths.get(0)))) {
            job.setStatus("PROCESSING");

            Splitter splitter = new Splitter();
            List<PDDocument> pages = splitter.split(document);

            int pageNumber = 1;
            for (PDDocument pageDoc : pages) {
                Path outputPath = resultsDir.resolve(job.getJobId() + "_page_" + pageNumber + ".pdf");
                try (pageDoc) {
                    pageDoc.save(outputPath.toFile());
                }
                outputFiles.add(outputPath.toString());
                pageNumber++;
            }

            job.setStatus("COMPLETED");
        } catch (IOException e) {
            job.setStatus("FAILED");
            throw e;
        }

        job.setOutputFile(outputFiles);
    }
}
