package org.example.textanalyzer.controller;

import org.example.textanalyzer.dto.AnalysisResultDto;
import org.example.textanalyzer.service.AnalysisService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/results")
public class ResultsController {

    private final AnalysisService analysisService;

    public ResultsController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisResultDto> getAnalysisResult(@PathVariable Long id) {
        AnalysisResultDto analysisResultDto = analysisService.getAnalysisResult(id);
        return ResponseEntity.ok(analysisResultDto);
    }

    @GetMapping
    public ResponseEntity<Page<AnalysisResultDto>> getAllAnalysisResults(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "5") int size) {
        Page<AnalysisResultDto> analysisResultDtoPage = analysisService.getAllAnalysisResults(page, size);
        return ResponseEntity.ok(analysisResultDtoPage);
    }

}
