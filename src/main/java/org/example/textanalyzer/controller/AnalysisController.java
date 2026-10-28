package org.example.textanalyzer.controller;

import jakarta.validation.Valid;
import org.example.textanalyzer.dto.AnalysisParams;
import org.example.textanalyzer.service.AnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analyze")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping
    public ResponseEntity<Long> analyze(@RequestBody @Valid AnalysisParams request) {
        Long analysisId = analysisService.startRestAnalysis(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(analysisId);
    }
}
