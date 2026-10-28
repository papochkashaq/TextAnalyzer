package org.example.textanalyzer.mapper;

import org.example.textanalyzer.entity.WordCountEntity;
import org.example.textanalyzer.model.WordCount;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WordCountMapper {

    WordCountEntity toWordCountEntity (WordCount wordCount);
}
