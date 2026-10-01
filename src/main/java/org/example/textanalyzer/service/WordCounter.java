package org.example.textanalyzer.service;

import org.example.textanalyzer.config.MultithreadingMode;
import org.example.textanalyzer.model.CountTopWordsResult;
import org.example.textanalyzer.model.WordCount;
import org.example.textanalyzer.model.WordCountResult;
import org.example.textanalyzer.parser.FileParser;
import org.example.textanalyzer.parser.ParsingError;
import org.example.textanalyzer.parser.ParsingOptions;
import org.example.textanalyzer.parser.ParsingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class WordCounter {

    private static final Logger logger = LoggerFactory.getLogger(WordCounter.class);
    private final FileParser fileParser;

    public WordCounter(FileParser fileParser) {
        this.fileParser = fileParser;
    }

    public CountTopWordsResult countTopWords(List<Path> paths, int topWords,
                                             ParsingOptions parsingOptions,
                                             int threads, MultithreadingMode mode) {
        Objects.requireNonNull(paths, "List of path must not be null");
        Objects.requireNonNull(parsingOptions, "Parsing options must not be null");
        if (topWords <= 0) {
            throw new IllegalArgumentException("Top words should be above 0");
        }

        WordCountResult wordCountResult;
        if (mode == MultithreadingMode.MULTI) {
            wordCountResult = countWordsMultithreading(paths, parsingOptions, threads);
        } else {
            wordCountResult = countWords(paths, parsingOptions);
        }

        Map<String, Integer> map = wordCountResult.wordCount();
        List<WordCount> wordCounts = map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry::getKey))
                .limit(topWords)
                .map(entry -> new WordCount(entry.getKey(), entry.getValue()))
                .toList();
        List<ParsingError> parsingErrors = wordCountResult.parsingErrors();
        return new CountTopWordsResult(wordCounts, parsingErrors);
    }

    private WordCountResult countWords(List<Path> paths, ParsingOptions parsingOptions) {
        Map<String, Integer> totals = new HashMap<>();
        List<ParsingError> errors = new ArrayList<>();
        for (Path path : paths) {
            ParsingResult parsingResult = fileParser.parseWithParsingOptions(path, parsingOptions);
            List<String> words = parsingResult.parsedWords();
            ParsingError parsingError = parsingResult.parsingError();
            if (parsingError != null) {
                errors.add(parsingError);
            }
            for (String word : words) {
                totals.merge(word, 1, Integer::sum);
            }
        }
        return new WordCountResult(totals, errors);
    }

    private WordCountResult countWordsMultithreading(List<Path> paths, ParsingOptions parsingOptions, int threads) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<Future<ParsingResult>> futures = new ArrayList<>();

        for (Path path : paths) {
            futures.add(executor.submit(() -> fileParser.parseWithParsingOptions(path, parsingOptions)));
        }

        Map<String, Integer> totals = new HashMap<>();
        List<ParsingError> errors = new ArrayList<>();

        for (Future<ParsingResult> future : futures) {
            try {
                ParsingResult result = future.get();
                for (String word : result.parsedWords()) {
                    totals.merge(word, 1, Integer::sum);
                }
                if (result.parsingError() != null) {
                    errors.add(result.parsingError());
                }
            } catch (ExecutionException e) {
                logger.error("Task failed for a file: {}", e.getCause().getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Thread interrupted");
                break;
            }
        }

        executor.shutdown();
        return new WordCountResult(totals, errors);
    }
}
