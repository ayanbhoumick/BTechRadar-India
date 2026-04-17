package com.careercompass.careercompass.service;

import com.careercompass.careercompass.model.CurriculumGapReport;
import com.careercompass.careercompass.model.JobListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CurriculumGapService {

    @Autowired
    private JobAggregatorService jobFetchService;

    @Autowired
    private SkillExtractorService skillExtractorService;

    private static final Map<String, List<String>> CURRICULUM = new HashMap<>();

    static {
        CURRICULUM.put("Computational Thinking & Programming",
                Arrays.asList("python", "object oriented", "algorithms"));

        CURRICULUM.put("Data Structures & Algorithms",
                Arrays.asList("data structures", "algorithms"));

        CURRICULUM.put("Design & Analysis of Algorithms",
                Arrays.asList("algorithms", "system design"));

        CURRICULUM.put("Computer Networks",
                Arrays.asList("rest api", "microservices"));

        CURRICULUM.put("Operating Systems",
                Arrays.asList("linux"));

        CURRICULUM.put("Database Management Systems",
                Arrays.asList("sql", "mysql", "postgresql", "mongodb"));

        CURRICULUM.put("Software Engineering",
                Arrays.asList("agile", "git", "system design"));

        CURRICULUM.put("Machine Learning",
                Arrays.asList("machine learning", "deep learning", "tensorflow",
                        "pandas", "numpy", "data analysis"));

        CURRICULUM.put("Web Technologies",
                Arrays.asList("html", "css", "javascript", "react"));

        CURRICULUM.put("Cloud & DevOps",
                Arrays.asList("aws", "azure", "docker", "kubernetes", "jenkins"));
    }

    public CurriculumGapReport analyzeCurriculum(List<String> studentSkills, String role, String city) {

        // Step 1 — Fetch jobs and extract descriptions (skip null/blank)
        List<JobListing> jobs = jobFetchService.fetchJobs(role, city);
        List<String> descriptions = new ArrayList<>();
        for (JobListing job : jobs) {
            String desc = job.getDescription();
            if (desc != null && !desc.isBlank()) descriptions.add(desc);
        }

        // Step 2 — Get demanded skills
        Map<String, Integer> skillMap = skillExtractorService.extractSkills(descriptions);
        Set<String> demandedSkills = new HashSet<>(skillMap.keySet());
        
        // Remove skills the student already knows
        if (studentSkills == null) studentSkills = new ArrayList<>();
        for (String s : studentSkills) {
            demandedSkills.remove(s.toLowerCase());
        }

        // Step 3 — All skills covered by curriculum
        Set<String> curriculumSkills = new HashSet<>();
        for (List<String> skills : CURRICULUM.values()) {
            curriculumSkills.addAll(skills);
        }

        // Step 4 — Covered vs uncovered
        List<String> coveredSkills = new ArrayList<>();
        List<String> uncoveredSkills = new ArrayList<>();

        for (String skill : demandedSkills) {
            if (curriculumSkills.contains(skill)) {
                coveredSkills.add(skill);
            } else {
                uncoveredSkills.add(skill);
            }
        }

        // Step 5 — Coverage percentage
        double coveragePercentage = demandedSkills.isEmpty() ? 0 :
                ((double) coveredSkills.size() / demandedSkills.size()) * 100;

        // Step 6 — Subject relevance scores
        Map<String, Double> subjectRelevanceScores = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : CURRICULUM.entrySet()) {
            String subject = entry.getKey();
            List<String> subjectSkills = entry.getValue();
            int hits = 0;
            for (String skill : subjectSkills) {
                if (demandedSkills.contains(skill)) hits++;
            }
            double score = ((double) hits / subjectSkills.size()) * 100;
            subjectRelevanceScores.put(subject, score);
        }

        return new CurriculumGapReport(coveragePercentage, coveredSkills,
                uncoveredSkills, subjectRelevanceScores);
    }
}