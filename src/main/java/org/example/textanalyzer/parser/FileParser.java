package org.example.textanalyzer.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class FileParser {

    private static final Pattern WORD_PATTERN = Pattern.compile("\\b[\\p{L}\\p{N}]+\\b");
    private static final Logger logger = LoggerFactory.getLogger(FileParser.class);

    public ParsingResult parseWithParsingOptions(Path file, ParsingOptions parsingOptions) {

        Objects.requireNonNull(file, "File path must not be null");
        Objects.requireNonNull(parsingOptions, "Parsing options must not be null");

        List<String> extractedWords = Collections.emptyList();
        Path fileName = file.getFileName();
        ParsingError parsingError = null;
        try {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            if (content.isBlank()) {
                parsingError = new ParsingError(fileName.toString(), "File is empty.");
                logger.warn("File is empty: {}", fileName);
            }
            extractedWords = extractWordsWithParsingOptions(content, parsingOptions);
        } catch (IOException e) {
            parsingError = new ParsingError(fileName.toString(), "Failed to read.");
            logger.warn("Failed to read a file: {}", fileName, e);
        }
        return new ParsingResult(extractedWords, parsingError);
    }

    public Set<String> parseStopWords(Path file) throws IOException {
        Set<String> extractedWords;
        String content = Files.readString(file, StandardCharsets.UTF_8);
            if (content.isBlank()) {
                logger.warn("File is empty: {}", file.getFileName());
            }
            extractedWords = extractStopWords(content);
        return extractedWords;
    }

    private List<String> extractWordsWithParsingOptions(String content, ParsingOptions parsingOptions) {
        return WORD_PATTERN.matcher(content)
                .results()
                .map(MatchResult::group)
                .map(s -> s.toLowerCase(Locale.ROOT))
                .filter(word -> word.length() >= parsingOptions.minWordLength())
                .filter(word -> !parsingOptions.stopWords().contains(word))
                .toList();
    }

    private Set<String> extractStopWords(String content) {
        return WORD_PATTERN.matcher(content)
                .results()
                .map(MatchResult::group)
                .distinct()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }
}
