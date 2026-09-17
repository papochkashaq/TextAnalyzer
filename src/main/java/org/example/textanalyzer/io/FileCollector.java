package org.example.textanalyzer.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

@Component
public class FileCollector {

    private static final Logger logger = LoggerFactory.getLogger(FileCollector.class);

    public List<Path> collectFiles(Path directory) {

        if (directory == null || !Files.exists(directory) || !Files.isDirectory(directory)) {
            logger.error("Directory not found or invalid: {}", directory);
            throw new IllegalArgumentException("Invalid directory");
        }


        try (Stream<Path> filesList = Files.list(directory)) {
            return filesList
                    .filter(Files::isRegularFile)
                    .toList();
        } catch (IOException e) {
            logger.error("Failed to read directory: {}", directory, e);
            throw new RuntimeException("Failed to list files", e);
        }

    }
}
