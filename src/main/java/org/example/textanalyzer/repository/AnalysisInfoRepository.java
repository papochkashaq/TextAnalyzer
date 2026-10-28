package org.example.textanalyzer.repository;

import org.example.textanalyzer.entity.AnalysisInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisInfoRepository extends JpaRepository<AnalysisInfoEntity, Long> {
}
