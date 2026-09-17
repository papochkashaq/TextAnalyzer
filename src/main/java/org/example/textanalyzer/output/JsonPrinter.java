package org.example.textanalyzer.output;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;

public class JsonPrinter {

    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(JsonPrinter.class);

    public JsonPrinter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void print(ResultOutput resultOutput, Path outputPath) {
        if (outputPath == null) {
            logger.error("Output path is empty: {}", outputPath);
            throw new IllegalArgumentException("Output path is empty");
        }
        if (Files.exists(outputPath)) {
            logger.warn("File will be overwritten: {}", outputPath);
        }

        try {
            objectMapper.writerWithDefaultPrettyPrinter().
                    writeValue(outputPath.toFile(), resultOutput);
            logger.info("Results saved to: {}", outputPath);
        } catch (JacksonException e) {
            logger.error("Failed to write in file: {}", outputPath, e);
            throw new RuntimeException("Failed to save results", e);
        }
    }
}
