package org.example.textanalyzer;

import org.example.textanalyzer.config.MultithreadingMode;
import org.example.textanalyzer.dto.AnalysisParams;
import org.example.textanalyzer.output.Printer;
import org.example.textanalyzer.dto.AnalysisResultDto;
import org.example.textanalyzer.service.AnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.nio.file.Path;
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

    private final AnalysisService analysisService;
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
    @CommandLine.Option(names = "--threads", description = "Number of threads", defaultValue = "2")
    private int threads;
    @CommandLine.Option(names = "--mode", description = "Multithreading mode", defaultValue = "MULTI")
    private MultithreadingMode mode;


    public TextAnalyzerRunner(AnalysisService analysisService, Printer printer) {
        this.analysisService = analysisService;
        this.printer = printer;
    }

    @Override
    public Integer call() {

        try {
            AnalysisResultDto analysisResultDto = analysisService.startCliAnalysis(new AnalysisParams(dirPath, minLength, top, outputPath, stopWordsPath, threads, mode));
            printer.print(analysisResultDto, outputPath);

            return 0;

        }
        catch (Exception e) {
            logger.error("Application failed: {}", e.getMessage(), e);
            System.err.println("Error: " + e.getMessage());
            return 1;
        }
    }
}
