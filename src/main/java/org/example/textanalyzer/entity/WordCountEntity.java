package org.example.textanalyzer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "word_count")
public class WordCountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_result_id", nullable = false)
    private AnalysisResultEntity analysisResultEntity;

    private String word;
    private int count;

    public WordCountEntity(Long id, AnalysisResultEntity analysisResultEntity, String word, int count) {
        this.id = id;
        this.analysisResultEntity = analysisResultEntity;
        this.word = word;
        this.count = count;
    }

    public WordCountEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnalysisResultEntity getAnalysisResultEntity() {
        return analysisResultEntity;
    }

    public void setAnalysisResultEntity(AnalysisResultEntity analysisResultEntity) {
        this.analysisResultEntity = analysisResultEntity;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
