package org.example.textanalyzer.model;

import org.example.textanalyzer.parser.ParsingError;

import java.util.List;

public record CountTopWordsResult(List<WordCount> wordCounts, List<ParsingError> parsingErrors) {
}
