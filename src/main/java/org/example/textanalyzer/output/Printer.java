package org.example.textanalyzer.output;

import org.example.textanalyzer.dto.AnalysisResultDto;

import java.nio.file.Path;

public interface Printer {

    void print(AnalysisResultDto analysisResultDto, Path outputPath);
}
