package com.careercompass.careercompass.model;
// coveredPercentage
// coveredSkills
// subjectRelevanceScores
// uncoveredSkills 

import java.util.List;
import java.util.Map;

public class CurriculumGapReport {

    private Double coveragePercentage;
    private List<String> coveredSkills;
    private List<String> uncoveredSkills;
    private Map <String, Double> subjectRelevanceScores;

    public CurriculumGapReport(Double coveragePercentage, List<String> coveredSkills, List<String> uncoveredSkills, Map<String, Double> subjectRelevanceScores){
        this.coveragePercentage = coveragePercentage;
        this.coveredSkills = coveredSkills;
        this.uncoveredSkills = uncoveredSkills;
        this.subjectRelevanceScores = subjectRelevanceScores;
    }

    // Getters

    public Double getCoveragePercentage(){return coveragePercentage; }
    public List<String> getCoveredSkills(){return coveredSkills; }
    public List<String> getUncoveredSkills(){return uncoveredSkills; }
    public Map<String, Double> getSubjectRelevanceScores(){return subjectRelevanceScores; }


    
}
