package org.example.textanalyzer.dto;

import org.example.textanalyzer.model.AnalysisInfo;
import org.example.textanalyzer.model.AnalysisStatus;
import org.example.textanalyzer.model.WordCount;
import org.example.textanalyzer.parser.ParsingError;

import java.util.List;

public record AnalysisResultDto(Long id, AnalysisInfo info, AnalysisStatus status, List<WordCount> words, List<ParsingError> errors) {}
