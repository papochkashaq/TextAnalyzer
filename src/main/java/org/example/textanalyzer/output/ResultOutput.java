package org.example.textanalyzer.output;

import org.example.textanalyzer.model.AnalysisInfo;
import org.example.textanalyzer.model.WordCount;
import org.example.textanalyzer.parser.ParsingError;

import java.util.List;

public record ResultOutput(AnalysisInfo analysisInfo, List<WordCount> words, List<ParsingError> errors) {}
