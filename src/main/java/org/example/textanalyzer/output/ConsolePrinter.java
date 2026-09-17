package org.example.textanalyzer.output;

import org.example.textanalyzer.model.WordCount;

import java.util.List;

public class ConsolePrinter {

    public void print(ResultOutput resultOutput) {
        List<WordCount> list = resultOutput.words();
        if (list == null || list.isEmpty()) {
            System.out.println("There are no words in these files.");
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            System.out.println(String.format("%d. %s — %d", i + 1, list.get(i).word(), list.get(i).count()));
        }
    }
}
