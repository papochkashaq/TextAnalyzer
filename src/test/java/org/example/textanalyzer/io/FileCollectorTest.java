package org.example.textanalyzer.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileCollectorTest {

    private final FileCollector fileCollector = new FileCollector();

    @TempDir
    Path tempDir;

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPathIsNull() {
        Path nullPath = null;

        assertThrows(IllegalArgumentException.class, () -> fileCollector.collectFiles(nullPath));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPathIsNotExisting() {
        Path notExistingPath = tempDir.resolve("not-existing-path");

        assertThrows(IllegalArgumentException.class, () -> fileCollector.collectFiles(notExistingPath));
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenPathIsNotDirectory() throws IOException {
        Path fileInsteadOfDirectory = Files.createFile(tempDir.resolve("file.txt"));

        assertThrows(IllegalArgumentException.class, () -> fileCollector.collectFiles(fileInsteadOfDirectory));
    }
}
