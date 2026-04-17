package com.careercompass.careercompass.model;
// matchedSkills
// missingSkills
// recommendations

import java.util.List;

public class SkillGapReport {
    private Double matchScore;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> recommendations;

    public SkillGapReport(Double matchScore, List<String> matchedSkills, List<String> missingSkills, List<String> recommendations){
        this.matchScore = matchScore;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.recommendations = recommendations;

    }

    // Getters

    public Double getMatchScore() {return matchScore; }
    public List<String> getMatchedSkills() {return matchedSkills; }
    public List<String> getMissingSkills() {return missingSkills; }
    public List<String> getRecommendations() {return recommendations; }


}
