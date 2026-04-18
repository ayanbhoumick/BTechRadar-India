package com.careercompass.careercompass.service;

import com.careercompass.careercompass.model.JobListing;
import com.careercompass.careercompass.model.SkillGapReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SkillGapService {

    @Autowired
    private JobAggregatorService jobFetchService;

    @Autowired
    private SkillExtractorService skillExtractorService;

    public SkillGapReport analyzeGap(List<String> userSkills, String role, String city) {

        // 1 — Fetch jobs
        List<JobListing> jobs = jobFetchService.fetchJobs(role, city);

        // 2 — Pull descriptions (skip null/blank)
        List<String> descriptions = new ArrayList<>();
        for (JobListing job : jobs) {
            String desc = job.getDescription();
            if (desc != null && !desc.isBlank()) descriptions.add(desc);
        }

        // 3 — Extract demanded skills
        Map<String, Integer> skillMap = skillExtractorService.extractSkills(descriptions);
        Set<String> demandedSkills = skillMap.keySet();

        // 4 — Normalize user skills to lowercase
        if (userSkills == null) userSkills = new ArrayList<>();
        Set<String> userSkillSet = new HashSet<>();
        for (String s : userSkills) {
            userSkillSet.add(s.toLowerCase());
        }

        // 5 — Matched and missing
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String skill : demandedSkills) {
            if (userSkillSet.contains(skill)) {
                matchedSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }

        // 6 — Match score
        double matchScore = demandedSkills.isEmpty() ? 0 :
                ((double) matchedSkills.size() / demandedSkills.size()) * 100;

        // 7 — Recommendations: top 5 missing skills sorted by market demand
        missingSkills.sort((a, b) -> skillMap.getOrDefault(b, 0) - skillMap.getOrDefault(a, 0));
        List<String> recommendations = missingSkills.size() > 5 ?
                new ArrayList<>(missingSkills.subList(0, 5)) : new ArrayList<>(missingSkills);

        return new SkillGapReport(matchScore, matchedSkills, missingSkills, recommendations);
    }
}