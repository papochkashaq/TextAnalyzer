package org.example.textanalyzer.mapper;

import org.example.textanalyzer.dto.AnalysisResultDto;
import org.example.textanalyzer.entity.AnalysisResultEntity;
import org.example.textanalyzer.model.AnalysisResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AnalysisResultMapper {

    AnalysisResultEntity toAnalysisResultEntity(AnalysisResultDto analysisResultDto);

    AnalysisResultEntity toAnalysisResultEntity(AnalysisResult analysisResult);

    AnalysisResultDto toAnalysisResultDto(AnalysisResultEntity analysisResultEntity);
}
