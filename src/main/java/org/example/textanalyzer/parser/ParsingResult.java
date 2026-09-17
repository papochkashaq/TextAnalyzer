package org.example.textanalyzer.parser;

import java.util.List;

public record ParsingResult(List<String> parsedWords, ParsingError parsingError) {
}
