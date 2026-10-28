package org.example.textanalyzer.output;

import org.example.textanalyzer.dto.AnalysisResultDto;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Path;

@Component
public class UnifiedPrinter implements Printer {

    private final ConsolePrinter consolePrinter;
    private final JsonPrinter jsonPrinter;
    private final ObjectMapper objectMapper;

    public UnifiedPrinter(ObjectMapper objectMapper) {
        this.consolePrinter = new ConsolePrinter();
        this.jsonPrinter = new JsonPrinter(objectMapper);
        this.objectMapper = objectMapper;
    }

    @Override
    public void print(AnalysisResultDto analysisResultDto, Path outputPath) {
        if (outputPath == null) {
            consolePrinter.print(analysisResultDto);
        } else {
            jsonPrinter.print(analysisResultDto, outputPath);
        }
    }
}
