package org.example.textanalyzer.model;

import org.example.textanalyzer.parser.ParsingError;

import java.util.List;
import java.util.Map;

public record WordCountResult(Map<String, Integer> wordCount, List<ParsingError> parsingErrors) {
}
