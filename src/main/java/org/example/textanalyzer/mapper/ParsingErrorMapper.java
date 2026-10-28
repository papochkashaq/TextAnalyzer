package org.example.textanalyzer.mapper;

import org.example.textanalyzer.entity.ParsingErrorEntity;
import org.example.textanalyzer.parser.ParsingError;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ParsingErrorMapper {

    ParsingErrorEntity toParsingErrorEntity(ParsingError parsingError);
}
