package org.example.textanalyzer.parser;

import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Path;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FileParserTest {

    private static final String blankFileForParsing = "blankFileForParsing.txt";
    private static final String fileForParsing = "fileForParsing.txt";
    private static final String stopWordsFile = "stopWords.txt";


    private final FileParser fileParser = new FileParser();

    @Test
    void shouldThrowWhenPathIsNull() {
        Path file = null;
        ParsingOptions parsingOptions = new ParsingOptions(1, Collections.emptySet());

        assertThrows(NullPointerException.class, () -> fileParser.parseWithParsingOptions(file, parsingOptions));
    }

    @Test
    void shouldThrowWhenParsingOptionsIsNull() throws Exception {
        Path file = getFileForParsing(blankFileForParsing);
        ParsingOptions parsingOptions = null;

        assertThrows(NullPointerException.class, () -> fileParser.parseWithParsingOptions(file, parsingOptions));
    }

    @Test
    void shouldReturnEmptyParsingResultWhenFileIsBlank() throws Exception {
        Path file = getFileForParsing(blankFileForParsing);

        ParsingOptions parsingOptions = new ParsingOptions(3, Collections.emptySet());
        ParsingResult parsingResult = fileParser.parseWithParsingOptions(file, parsingOptions);
        ParsingError parsingError = parsingResult.parsingError();
        ParsingError expectedParsingError = new ParsingError(file.getFileName().toString(), "File is empty.");

        assertNotNull(parsingError);
        assertEquals(expectedParsingError, parsingError);
    }

    @Test
    void shouldReturnParsingResultWith5Words() throws Exception {
        List<String> expectedParsedWords = List.of("test1", "test2", "тест3", "1234", "test4");
        Path file = getFileForParsing(fileForParsing);
        ParsingOptions parsingOptions = new ParsingOptions(3, Collections.emptySet());
        ParsingResult parsingResult = fileParser.parseWithParsingOptions(file, parsingOptions);
        List<String> parsedWords = parsingResult.parsedWords();

        assertThat(parsedWords)
                .isNotNull()
                .isNotEmpty()
                .hasSize(5)
                .containsExactlyInAnyOrderElementsOf(expectedParsedWords);
    }

    @Test
    void shouldReturnStopWords() throws Exception {
        Set<String> expectedParsedWords = Set.of("test1", "test2", "тест3");
        Path file = getFileForParsing(stopWordsFile);
        Set<String> stopWords = fileParser.parseStopWords(file);

        assertThat(stopWords)
                .isNotNull()
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(expectedParsedWords);
    }

    @Test
    void shouldReturnParsingResultWithoutStopWords() throws Exception {
        List<String> expectedParsedWords = List.of("1234", "test4");
        Path file = getFileForParsing(fileForParsing);
        Path stopWordsPath = getFileForParsing(stopWordsFile);

        Set<String> stopWords = fileParser.parseStopWords(stopWordsPath);
        ParsingOptions parsingOptions = new ParsingOptions(3, stopWords);
        ParsingResult parsingResult = fileParser.parseWithParsingOptions(file, parsingOptions);
        List<String> parsedWords = parsingResult.parsedWords();

        assertThat(parsedWords)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyInAnyOrderElementsOf(expectedParsedWords);
    }

    @Test
    void shouldReturnParsingResultWith4WordsBecauseOfMinWordsLength() throws Exception {
        List<String> expectedParsedWords = List.of("test1", "test2", "тест3", "test4");
        Path file = getFileForParsing(fileForParsing);

        ParsingOptions parsingOptions = new ParsingOptions(5, Collections.emptySet());
        ParsingResult parsingResult = fileParser.parseWithParsingOptions(file, parsingOptions);
        List<String> parsedWords = parsingResult.parsedWords();

        assertThat(parsedWords)
                .isNotNull()
                .isNotEmpty()
                .hasSize(4)
                .containsExactlyInAnyOrderElementsOf(expectedParsedWords);
    }

    @Test
    void shouldReturnParsingResultWithAllWordsBecauseStopWordsIsEmpty() throws Exception {
        List<String> expectedParsedWords = List.of("1234", "test1", "test2", "тест3", "test4", "te");
        Path file = getFileForParsing(fileForParsing);
        Path stopWordsPath = getFileForParsing(blankFileForParsing);

        Set<String> stopWords = fileParser.parseStopWords(stopWordsPath);
        ParsingOptions parsingOptions = new ParsingOptions(0, stopWords);
        ParsingResult parsingResult = fileParser.parseWithParsingOptions(file, parsingOptions);
        List<String> parsedWords = parsingResult.parsedWords();

        assertThat(parsedWords)
                .isNotNull()
                .isNotEmpty()
                .hasSize(6)
                .containsExactlyInAnyOrderElementsOf(expectedParsedWords);
    }

    @Test
    void shouldReturnParsingResultWithoutWordsBecauseTheyEqualStopWords() throws Exception {
        Path file = getFileForParsing(stopWordsFile);
        Path stopWordsPath = getFileForParsing(stopWordsFile);

        Set<String> stopWords = fileParser.parseStopWords(stopWordsPath);
        ParsingOptions parsingOptions = new ParsingOptions(0, stopWords);
        ParsingResult parsingResult = fileParser.parseWithParsingOptions(file, parsingOptions);
        List<String> parsedWords = parsingResult.parsedWords();

        assertThat(parsedWords)
                .isNotNull()
                .isEmpty();
    }

    private Path getFileForParsing(String file) throws Exception {
        URL resourceUrl = getClass().getClassLoader().getResource(file);
        assertNotNull(resourceUrl, "Test resource not found");
        return Path.of(resourceUrl.toURI());
    }
}
