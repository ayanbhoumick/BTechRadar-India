package com.careercompass.careercompass.model;

import java.util.List;

public class ResumeAnalysisReport {

    private int atsScore;
    private int qualityScore;
    private String atsGrade;
    private String qualityGrade;
    private List<String> matchedKeywords;
    private List<String> missingKeywords;
    private List<String> qualityIssues;
    private List<String> suggestions;
    private int wordCount;
    private int pageCount;

    public ResumeAnalysisReport(
            int atsScore, int qualityScore,
            String atsGrade, String qualityGrade,
            List<String> matchedKeywords, List<String> missingKeywords,
            List<String> qualityIssues, List<String> suggestions,
            int wordCount, int pageCount) {
        this.atsScore       = atsScore;
        this.qualityScore   = qualityScore;
        this.atsGrade       = atsGrade;
        this.qualityGrade   = qualityGrade;
        this.matchedKeywords = matchedKeywords;
        this.missingKeywords = missingKeywords;
        this.qualityIssues  = qualityIssues;
        this.suggestions    = suggestions;
        this.wordCount      = wordCount;
        this.pageCount      = pageCount;
    }

    public int getAtsScore()              { return atsScore; }
    public int getQualityScore()          { return qualityScore; }
    public String getAtsGrade()           { return atsGrade; }
    public String getQualityGrade()       { return qualityGrade; }
    public List<String> getMatchedKeywords() { return matchedKeywords; }
    public List<String> getMissingKeywords() { return missingKeywords; }
    public List<String> getQualityIssues()   { return qualityIssues; }
    public List<String> getSuggestions()     { return suggestions; }
    public int getWordCount()             { return wordCount; }
    public int getPageCount()             { return pageCount; }
}
