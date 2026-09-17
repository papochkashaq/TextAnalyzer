package org.example.textanalyzer.service;

import org.example.textanalyzer.model.CountTopWordsResult;
import org.example.textanalyzer.model.WordCount;
import org.example.textanalyzer.parser.FileParser;
import org.example.textanalyzer.parser.ParsingError;
import org.example.textanalyzer.parser.ParsingOptions;
import org.example.textanalyzer.parser.ParsingResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WordCounterTest {

    @Mock
    private FileParser fileParser;
    @InjectMocks
    private WordCounter wordCounter;

    @Test
    void shouldThrowWhenListOfPathsIsNull() {
        List<Path> paths = null;
        ParsingOptions parsingOptions = new ParsingOptions(0, Collections.emptySet());

        assertThrows(NullPointerException.class, () -> wordCounter.countTopWords(paths, 3, parsingOptions));
    }

    @Test
    void shouldThrowWhenParsingOptionsIsNull() {
        List<Path> paths = Collections.emptyList();
        ParsingOptions parsingOptions = null;

        assertThrows(NullPointerException.class, () -> wordCounter.countTopWords(paths, 3, parsingOptions));
    }

    @Test
    void shouldThrowWhenTopWordsLessThanZero() {
        ParsingOptions parsingOptions = new ParsingOptions(0, Collections.emptySet());

        assertThrows(IllegalArgumentException.class, () -> wordCounter.countTopWords(Collections.emptyList(), 0, parsingOptions));
        assertThrows(IllegalArgumentException.class, () -> wordCounter.countTopWords(Collections.emptyList(), -10, parsingOptions));
    }

    @Test
    void shouldReturnCorrectResult() {
        Path file1 = Path.of("file1.txt");
        Path file2 = Path.of("file2.txt");

        when(fileParser.parseWithParsingOptions(eq(file1), any()))
                .thenReturn(new ParsingResult(List.of("test", "test", "word", "word"), null));
        when(fileParser.parseWithParsingOptions(eq(file2), any()))
                .thenReturn(new ParsingResult(List.of("test", "word", "word"), null));

        CountTopWordsResult result = wordCounter.countTopWords(
                List.of(file1, file2), 10, new ParsingOptions(0, Collections.emptySet()));

        assertThat(result).isNotNull();
        assertThat(result.wordCounts()).hasSize(2);
        assertThat(result.wordCounts().get(0).word()).isEqualTo("word");
        assertThat(result.wordCounts().get(0).count()).isEqualTo(4);
        assertThat(result.wordCounts().get(1).word()).isEqualTo("test");
        assertThat(result.wordCounts().get(1).count()).isEqualTo(3);
        assertThat(result.parsingErrors()).isEmpty();
    }

    @Test
    void shouldReturnEmptyResultWhenListOfPathsIsEmpty() {
        CountTopWordsResult result = wordCounter.countTopWords(
                List.of(), 10, new ParsingOptions(0, Collections.emptySet()));

        assertThat(result).isNotNull();
        assertThat(result.wordCounts()).isEmpty();
        assertThat(result.parsingErrors()).isEmpty();
        verifyNoInteractions(fileParser);
    }

    @Test
    void shouldReturnEmptyResultWhenAllFilesWithParsingErrors() {
        Path file1 = Path.of("file1.txt");
        Path file2 = Path.of("file2.txt");

        when(fileParser.parseWithParsingOptions(eq(file1), any()))
                .thenReturn(new ParsingResult(List.of(), new ParsingError(file1.getFileName().toString(), "Failed to read.")));
        when(fileParser.parseWithParsingOptions(eq(file2), any()))
                .thenReturn(new ParsingResult(List.of(), new ParsingError(file1.getFileName().toString(), "File is empty.")));

        CountTopWordsResult result = wordCounter.countTopWords(
                List.of(file1, file2), 10, new ParsingOptions(0, Collections.emptySet()));

        assertThat(result).isNotNull();
        assertThat(result.wordCounts()).isEmpty();
        assertThat(result.parsingErrors()).isNotEmpty();
        assertThat(result.parsingErrors().get(0).message()).isEqualTo("Failed to read.");
        assertThat(result.parsingErrors().get(1).message()).isEqualTo("File is empty.");
    }

    @Test
    void shouldCollectErrorsFromMultipleFiles() throws Exception {
        Path validFile = Path.of("validFile.txt");
        Path invalidFile = Path.of("invalidFile.txt");
        Path emptyFile = Path.of("emptyFile.txt");

        List<Path> paths = List.of(validFile, invalidFile, emptyFile);
        ParsingOptions parsingOptions = new ParsingOptions(0, Collections.emptySet());

        when(fileParser.parseWithParsingOptions(eq(validFile), any()))
                .thenReturn(new ParsingResult(List.of("test"), null));
        when(fileParser.parseWithParsingOptions(eq(invalidFile), any()))
                .thenReturn(new ParsingResult(List.of(),
                        new ParsingError(invalidFile.getFileName().toString(), "Failed to read.")));
        when(fileParser.parseWithParsingOptions(eq(emptyFile), any()))
                .thenReturn(new ParsingResult(List.of(),
                        new ParsingError(emptyFile.getFileName().toString(), "File is empty.")));

        CountTopWordsResult result = wordCounter.countTopWords(paths, 10, parsingOptions);
        List<ParsingError> parsingErrorList = result.parsingErrors();

        assertThat(parsingErrorList).isNotNull()
                .hasSize(2);
        assertThat(parsingErrorList)
                .extracting(ParsingError::file)
                .containsExactlyInAnyOrder("invalidFile.txt", "emptyFile.txt");
    }

    @Test
    void shouldRespectTopWordsLimit() {
        Path file = Path.of("file.txt");

        when(fileParser.parseWithParsingOptions(eq(file), any()))
                .thenReturn(new ParsingResult(List.of("test1", "test1", "test1", "test2", "test2", "test3", "test4"), null));

        ParsingOptions parsingOptions = new ParsingOptions(0, Collections.emptySet());
        CountTopWordsResult result = wordCounter.countTopWords(List.of(file), 2, parsingOptions);
        List<WordCount> wordCountList = result.wordCounts();

        assertThat(wordCountList)
                .isNotNull()
                .hasSize(2);
        assertThat(wordCountList)
                .extracting(WordCount::word)
                .containsExactly("test1", "test2");
    }

    @Test
    void shouldReturnAllWordsWhenTopWordsExceedsUniqueWords() {
        Path file = Path.of("file.txt");

        when(fileParser.parseWithParsingOptions(eq(file), any()))
                .thenReturn(new ParsingResult(List.of("test1", "test1", "test1", "test2", "test2", "test3"), null));


        ParsingOptions parsingOptions = new ParsingOptions(0, Collections.emptySet());
        CountTopWordsResult result = wordCounter.countTopWords(List.of(file), 10, parsingOptions);
        List<WordCount> wordCountList = result.wordCounts();

        assertThat(wordCountList)
                .isNotNull()
                .hasSize(3);

        assertThat(wordCountList)
                .extracting(WordCount::word)
                .containsExactly("test1", "test2", "test3");
    }

}
