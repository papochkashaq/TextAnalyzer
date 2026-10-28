package org.example.textanalyzer.entity;

import jakarta.persistence.*;
import org.example.textanalyzer.model.AnalysisStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "analysis_result")
public class AnalysisResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "analysis_info_id", nullable = false, unique = true)
    private AnalysisInfoEntity analysisInfoEntity;

    @Enumerated
    private AnalysisStatus analysisStatus;

    @OneToMany(mappedBy = "analysisResultEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WordCountEntity> words = new ArrayList<>();

    @OneToMany(mappedBy = "analysisResultEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParsingErrorEntity> errors = new ArrayList<>();

    public AnalysisResultEntity(Long id, AnalysisInfoEntity analysisInfoEntity, AnalysisStatus analysisStatus, List<WordCountEntity> words, List<ParsingErrorEntity> errors) {
        this.id = id;
        this.analysisInfoEntity = analysisInfoEntity;
        this.analysisStatus = analysisStatus;
        this.words = words;
        this.errors = errors;
    }

    public AnalysisResultEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnalysisInfoEntity getAnalysisInfoEntity() {
        return analysisInfoEntity;
    }

    public void setAnalysisInfoEntity(AnalysisInfoEntity analysisInfoEntity) {
        this.analysisInfoEntity = analysisInfoEntity;
    }

    public List<WordCountEntity> getWords() {
        return words;
    }

    public void setWords(List<WordCountEntity> words) {
        this.words = words;
    }

    public List<ParsingErrorEntity> getErrors() {
        return errors;
    }

    public void setErrors(List<ParsingErrorEntity> errors) {
        this.errors = errors;
    }

    public AnalysisStatus getAnalysisStatus() {
        return analysisStatus;
    }

    public void setAnalysisStatus(AnalysisStatus analysisStatus) {
        this.analysisStatus = analysisStatus;
    }
}
