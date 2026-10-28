package org.example.textanalyzer.model;

import org.example.textanalyzer.parser.ParsingError;

import java.util.List;

public record AnalysisResult(AnalysisInfo analysisInfo, AnalysisStatus status, List<WordCount> words, List<ParsingError> errors) {}
