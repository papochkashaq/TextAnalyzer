package org.example.textanalyzer.output;

import org.example.textanalyzer.model.AnalysisInfo;
import org.example.textanalyzer.dto.AnalysisResultDto;
import org.example.textanalyzer.model.WordCount;

import java.util.List;

public class ConsolePrinter {

    public void print(AnalysisResultDto analysisResultDto) {
        List<WordCount> list = analysisResultDto.words();
        AnalysisInfo analysisInfo = analysisResultDto.info();
        if (list == null || list.isEmpty()) {
            System.out.println("There are no words in these files.");
            return;
        }
        System.out.println(String.format("""
                        Mode: %s (%d workers)
                        Processed %d files in %d ms
                        Top %d words (min length = %d):
                        """,
                analysisInfo.mode(), analysisInfo.threads(),
                analysisInfo.processedFiles(), analysisInfo.executionTimeMs(),
                analysisInfo.topCount(), analysisInfo.minWordLength()));
        for (int i = 0; i < list.size(); i++) {
            System.out.println(String.format("%d. %s — %d", i + 1, list.get(i).word(), list.get(i).count()));
        }
    }
}
