package org.example.textanalyzer.parser;

import java.util.Set;

public record ParsingOptions (int minWordLength, Set<String> stopWords) {}
