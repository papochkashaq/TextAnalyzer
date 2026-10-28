package org.example.textanalyzer.repository;

import org.example.textanalyzer.entity.AnalysisResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResultEntity, Long> {
}
