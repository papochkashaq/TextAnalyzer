package org.example.textanalyzer.model;

import org.example.textanalyzer.config.MultithreadingMode;

public record AnalysisInfo(String directory, int minWordLength, int topCount,
                           MultithreadingMode mode, int threads, int processedFiles,
                           long executionTimeMs) {
}
