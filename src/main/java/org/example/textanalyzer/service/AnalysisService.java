package org.example.textanalyzer.service;

import org.example.textanalyzer.entity.AnalysisInfoEntity;
import org.example.textanalyzer.mapper.ParsingErrorMapper;
import org.example.textanalyzer.mapper.WordCountMapper;
import org.example.textanalyzer.model.*;
import org.example.textanalyzer.dto.AnalysisParams;
import org.example.textanalyzer.entity.AnalysisResultEntity;
import org.example.textanalyzer.io.FileCollector;
import org.example.textanalyzer.mapper.AnalysisResultMapper;
import org.example.textanalyzer.dto.AnalysisResultDto;
import org.example.textanalyzer.parser.FileParser;
import org.example.textanalyzer.parser.ParsingError;
import org.example.textanalyzer.parser.ParsingOptions;
import org.example.textanalyzer.repository.AnalysisInfoRepository;
import org.example.textanalyzer.repository.AnalysisResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisService.class);

    private final FileParser fileParser;
    private final FileCollector fileCollector;
    private final WordCounter wordCounter;
    private final AnalysisResultRepository resultRepository;
    private final AnalysisInfoRepository infoRepository;
    private final AnalysisResultMapper analysisResultMapper;
    private final WordCountMapper wordCountMapper;
    private final ParsingErrorMapper parsingErrorMapper;

    public AnalysisService(FileParser fileParser, FileCollector fileCollector, WordCounter wordCounter, AnalysisResultRepository resultRepository, AnalysisInfoRepository infoRepository, AnalysisResultMapper analysisResultMapper, WordCountMapper wordCountMapper, ParsingErrorMapper parsingErrorMapper) {
        this.fileParser = fileParser;
        this.fileCollector = fileCollector;
        this.wordCounter = wordCounter;
        this.resultRepository = resultRepository;
        this.infoRepository = infoRepository;
        this.analysisResultMapper = analysisResultMapper;
        this.wordCountMapper = wordCountMapper;
        this.parsingErrorMapper = parsingErrorMapper;
    }

    public AnalysisResult startCliAnalysis(AnalysisParams params) {
        return makeAnalysis(params);
    }

    @Transactional
    public Long startRestAnalysis(AnalysisParams params) {
        AnalysisInfoEntity infoEntity = new AnalysisInfoEntity();
        infoEntity.setDirectory(params.dirPath().toString());
        infoEntity.setMinWordLength(params.minLength());
        infoEntity.setTopCount(params.top());
        infoEntity.setMode(params.mode());

        infoRepository.save(infoEntity);

        AnalysisResultEntity resultEntity = new AnalysisResultEntity();
        resultEntity.setAnalysisInfoEntity(infoEntity);
        resultEntity.setAnalysisStatus(AnalysisStatus.PENDING);

        resultRepository.save(resultEntity);
        Long analysisId = resultEntity.getId();
        executeAnalysisAsync(analysisId, params);
        return analysisId;
    }

    @Transactional(readOnly = true)
    public AnalysisResultDto getAnalysisResult(Long id) {
        AnalysisResultEntity resultEntity = resultRepository.findById(id)
                .orElseThrow();
        AnalysisResultDto resultDto = convertToAnalysisResultDto(resultEntity);

        AnalysisStatus status = resultDto.status();
        if (status != AnalysisStatus.COMPLETED) {
            return new AnalysisResultDto(id, resultDto.info(), resultDto.status(), resultDto.words(), resultDto.errors());
        }

        return resultDto;
    }

    @Transactional(readOnly = true)
    public Page<AnalysisResultDto> getAllAnalysisResults(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AnalysisResultEntity> analysisResultEntityPage = resultRepository.findAll(pageable);
        return analysisResultEntityPage.map(this::convertToAnalysisResultDto);
    }

    private AnalysisResult makeAnalysis(AnalysisParams params) {

        try {
            logger.info("Directory: {}, minWordLength: {}, topWords: {}", params.dirPath(), params.minLength(), params.top());

            Set<String> stopWords = new HashSet<>();
            if (params.stopWordsPath() != null) {
                stopWords = fileParser.parseStopWords(params.stopWordsPath());
            }

            List<Path> files = fileCollector.collectFiles(params.dirPath());
            ParsingOptions parsingOptions = new ParsingOptions(params.minLength(), stopWords);

            long startTime = System.currentTimeMillis();
            CountTopWordsResult countTopWordsResult = wordCounter.countTopWords(files, params.top(), parsingOptions, params.threads(), params.mode());
            long executionTime = System.currentTimeMillis() - startTime;

            List<WordCount> wordCounts = countTopWordsResult.wordCounts();
            List<ParsingError> parsingErrors = countTopWordsResult.parsingErrors();
            AnalysisInfo analysisInfo = new AnalysisInfo(params.dirPath().getFileName().toString(), params.minLength(), params.top(), params.mode(), params.threads(), files.size(), executionTime);

            return new AnalysisResult(analysisInfo, AnalysisStatus.COMPLETED, wordCounts, parsingErrors);
        } catch (IOException e) {
            logger.error("Failed to read file: {}", e.getMessage());
            return new AnalysisResult(new AnalysisInfo(params.dirPath().getFileName().toString(), params.minLength(), params.top(), params.mode(), params.threads(), 1, 0), AnalysisStatus.FAILED, List.of(), List.of());
        }
    }

    @Async
    private AnalysisResult executeAnalysisAsync(Long analysisId, AnalysisParams params) {
        AnalysisResultEntity resultEntity = resultRepository.findById(analysisId).orElseThrow();
        resultEntity.setAnalysisStatus(AnalysisStatus.RUNNING);
        resultRepository.save(resultEntity);

        AnalysisResult result = makeAnalysis(params);
        resultEntity.setAnalysisStatus(result.status());
        resultEntity.getAnalysisInfoEntity().setProcessedFiles(result.analysisInfo().processedFiles());
        resultEntity.getAnalysisInfoEntity().setExecutionTimeMs(result.analysisInfo().executionTimeMs());
        resultEntity.setWords(result.words().stream().map(wordCountMapper::toWordCountEntity).toList());
        resultEntity.setErrors(result.errors().stream().map(parsingErrorMapper::toParsingErrorEntity).toList());
        resultRepository.save(resultEntity);

        return result;
    }

    private AnalysisResultDto convertToAnalysisResultDto(AnalysisResultEntity analysisResultEntity) {
        return analysisResultMapper.toAnalysisResultDto(analysisResultEntity);
    }

}
