package org.example.textanalyzer.entity;

import jakarta.persistence.*;
import org.example.textanalyzer.config.MultithreadingMode;

@Entity
@Table(name = "analysis_info")
public class AnalysisInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "analysisInfoEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private AnalysisResultEntity analysisResultEntity;

    private String directory;
    private int minWordLength;
    private int topCount;
    @Enumerated(EnumType.STRING)
    private MultithreadingMode mode;
    private int threads;
    private int processedFiles;
    private long executionTimeMs;

    public AnalysisInfoEntity(Long id, String directory, int minWordLength, int topCount, MultithreadingMode mode, int threads, int processedFiles, long executionTimeMs) {
        this.id = id;
        this.directory = directory;
        this.minWordLength = minWordLength;
        this.topCount = topCount;
        this.mode = mode;
        this.threads = threads;
        this.processedFiles = processedFiles;
        this.executionTimeMs = executionTimeMs;
    }

    public AnalysisInfoEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public int getMinWordLength() {
        return minWordLength;
    }

    public void setMinWordLength(int minWordLength) {
        this.minWordLength = minWordLength;
    }

    public int getTopCount() {
        return topCount;
    }

    public void setTopCount(int topCount) {
        this.topCount = topCount;
    }

    public MultithreadingMode getMode() {
        return mode;
    }

    public void setMode(MultithreadingMode mode) {
        this.mode = mode;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public int getProcessedFiles() {
        return processedFiles;
    }

    public void setProcessedFiles(int processedFiles) {
        this.processedFiles = processedFiles;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
}
