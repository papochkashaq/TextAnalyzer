package org.example.textanalyzer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "parsing_error")
public class ParsingErrorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_result_id", nullable = false)
    private AnalysisResultEntity analysisResultEntity;

    private String file;
    private String message;

    public ParsingErrorEntity(Long id, AnalysisResultEntity analysisResultEntity, String file, String message) {
        this.id = id;
        this.analysisResultEntity = analysisResultEntity;
        this.file = file;
        this.message = message;
    }

    public ParsingErrorEntity() {
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

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
