package org.example.textanalyzer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.textanalyzer.config.MultithreadingMode;

import java.nio.file.Path;

public record AnalysisParams(

        @NotNull(message = "Directory is required") Path dirPath,

        @NotNull(message = "Minimum length is required") @Positive(message = "Minimum length should be more than a zero") int minLength,

        @NotNull(message = "Number of top words is required") @Positive(message = "Number of top words should be more than a zero") int top,

        @NotBlank(message = "Output path should not be blank") Path outputPath,

        @NotBlank(message = "Stopwords path should not be blank") Path stopWordsPath,

        @Positive(message = "Number of threads should be more than a zero") int threads,

        @NotBlank(message = "Mode should not be blank") MultithreadingMode mode) {
}
