package org.example.textanalyzer;

import org.example.textanalyzer.io.FileCollector;
import org.example.textanalyzer.model.AnalysisInfo;
import org.example.textanalyzer.model.CountTopWordsResult;
import org.example.textanalyzer.model.WordCount;
import org.example.textanalyzer.output.Printer;
import org.example.textanalyzer.output.ResultOutput;
import org.example.textanalyzer.parser.FileParser;
import org.example.textanalyzer.parser.ParsingError;
import org.example.textanalyzer.parser.ParsingOptions;
import org.example.textanalyzer.service.WordCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Callable;

@Component
@CommandLine.Command(
        name = "text-analyzer",
        description = "Analyzes text files in a directory",
        mixinStandardHelpOptions = true,
        exitCodeOnInvalidInput = 1,
        exitCodeOnExecutionException = 1
)
public class TextAnalyzerRunner implements Callable<Integer> {

    private final FileParser fileParser;
    private final FileCollector fileCollector;
    private final WordCounter wordCounter;
    private final Printer printer;
    private static final Logger logger = LoggerFactory.getLogger(TextAnalyzerRunner.class);

    @CommandLine.Option(names = "--dir", description = "Path to directory with text files", required = true)
    private Path dirPath;
    @CommandLine.Option(names = "--min-length", description = "Minimum word length", required = true)
    private int minLength;
    @CommandLine.Option(names = "--top", description = "Number of top words", required = true)
    private int top;

    @CommandLine.Option(names = "--output", description = "Path to JSON output file")
    private Path outputPath;
    @CommandLine.Option(names = "--stopwords", description = "Path to file with stop words")
    private Path stopWordsPath;


    public TextAnalyzerRunner(FileParser fileParser, FileCollector fileCollector, WordCounter wordCounter, Printer printer) {
        this.fileParser = fileParser;
        this.fileCollector = fileCollector;
        this.wordCounter = wordCounter;
        this.printer = printer;
    }

    @Override
    public Integer call() {

        try {
            logger.info("Directory: {}, minWordLength: {}, topWords: {}", dirPath, minLength, top);

            Set<String> stopWords = new HashSet<>();
            if (stopWordsPath != null) {
                stopWords = fileParser.parseStopWords(stopWordsPath);
            }

            List<Path> files = fileCollector.collectFiles(dirPath);
            ParsingOptions parsingOptions = new ParsingOptions(minLength, stopWords);
            CountTopWordsResult countTopWordsResult = wordCounter.countTopWords(files, top, parsingOptions);
            List<WordCount> wordCounts = countTopWordsResult.wordCounts();
            List<ParsingError> parsingErrors = countTopWordsResult.parsingErrors();
            AnalysisInfo analysisInfo = new AnalysisInfo(dirPath.getFileName().toString(), minLength, top);
            ResultOutput resultOutput = new ResultOutput(analysisInfo, wordCounts, parsingErrors);
            printer.print(resultOutput, outputPath);

            return 0;
        } catch (IOException e) {
            logger.error("Failed to read file: {}", e.getMessage());
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
        catch (Exception e) {
            logger.error("Application failed: {}", e.getMessage(), e);
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }
}
