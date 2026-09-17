package org.example.textanalyzer.output;

import java.nio.file.Path;

public interface Printer {

    void print(ResultOutput resultOutput, Path outputPath);
}
