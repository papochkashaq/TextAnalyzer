package org.example.textanalyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TextAnalyzerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TextAnalyzerApplication.class, args);
    }

}
