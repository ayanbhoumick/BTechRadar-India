package com.careercompass.careercompass.controller;

import com.careercompass.careercompass.model.ResumeAnalysisReport;
import com.careercompass.careercompass.service.ATSScoringService;
import com.careercompass.careercompass.service.ResumeParserService;
import com.careercompass.careercompass.service.ResumeQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    @Autowired private ResumeParserService  parser;
    @Autowired private ATSScoringService    ats;
    @Autowired private ResumeQualityService quality;

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResumeAnalysisReport> analyze(
            @RequestParam("file")                          MultipartFile file,
            @RequestParam("role")                          String        role,
            @RequestParam(value = "city",     required = false) String   city,
            @RequestParam(value = "jd",       required = false) String   jobDescription)
            throws IOException {

        if (file.isEmpty()) return ResponseEntity.badRequest().build();

        ResumeParserService.ParseResult parsed = parser.parse(file);

        ATSScoringService.ATSResult     atsResult     = ats.score(parsed.text, role, jobDescription);
        ResumeQualityService.QualityResult qualResult = quality.assess(parsed.text, parsed.pageCount);

        ResumeAnalysisReport report = new ResumeAnalysisReport(
            atsResult.score,
            qualResult.score,
            atsResult.grade,
            qualResult.grade,
            atsResult.matched,
            atsResult.missing,
            qualResult.issues,
            qualResult.suggestions,
            qualResult.wordCount,
            parsed.pageCount
        );

        return ResponseEntity.ok(report);
    }
}
