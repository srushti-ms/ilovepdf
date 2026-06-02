package com.example.ILovePdf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDetails {

    private UUID jobId;

    private String operation;

    private String status;

    private List<String> inputFiles;

    private List<String> outputFile;

}
