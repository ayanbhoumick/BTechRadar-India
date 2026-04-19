package com.careercompass.careercompass.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ATSScoringService {

    private static final Map<String, List<String>> ROLE_SKILLS = new HashMap<>();

    static {
        ROLE_SKILLS.put("Frontend Engineer", Arrays.asList(
            "react", "angular", "vue", "javascript", "html", "css", "typescript"
        ));
        ROLE_SKILLS.put("Backend Engineer", Arrays.asList(
            "java", "python", "node.js", "spring boot", "django", "rest api",
            "microservices", "sql", "postgresql", "mongodb", "redis"
        ));
        ROLE_SKILLS.put("Full Stack Engineer", Arrays.asList(
            "react", "javascript", "html", "css", "typescript",
            "node.js", "rest api", "sql", "mongodb", "docker", "git"
        ));
        ROLE_SKILLS.put("AI Engineer", Arrays.asList(
            "python", "machine learning", "deep learning", "tensorflow",
            "pandas", "numpy", "docker", "kubernetes"
        ));
        ROLE_SKILLS.put("AI and Data Scientist", Arrays.asList(
            "python", "machine learning", "deep learning", "tensorflow",
            "pandas", "numpy", "data analysis", "tableau"
        ));
        ROLE_SKILLS.put("Data Engineer", Arrays.asList(
            "python", "sql", "postgresql", "mongodb", "pandas",
            "numpy", "docker", "kubernetes", "aws"
        ));
        ROLE_SKILLS.put("Data Analyst", Arrays.asList(
            "sql", "python", "pandas", "numpy", "data analysis", "tableau", "mysql"
        ));
        ROLE_SKILLS.put("Machine Learning Engineer", Arrays.asList(
            "python", "machine learning", "deep learning", "tensorflow",
            "pandas", "numpy", "docker", "kubernetes"
        ));
        ROLE_SKILLS.put("MLOps Engineer", Arrays.asList(
            "python", "docker", "kubernetes", "aws", "azure",
            "jenkins", "linux", "machine learning"
        ));
        ROLE_SKILLS.put("BI Analyst", Arrays.asList(
            "sql", "tableau", "data analysis", "mysql", "postgresql", "python"
        ));
        ROLE_SKILLS.put("DevOps Engineer", Arrays.asList(
            "docker", "kubernetes", "aws", "azure", "git", "jenkins", "linux"
        ));
        ROLE_SKILLS.put("DevSecOps Engineer", Arrays.asList(
            "docker", "kubernetes", "aws", "linux", "git", "jenkins", "python"
        ));
        ROLE_SKILLS.put("Cyber Security Engineer", Arrays.asList(
            "linux", "python", "docker", "aws", "git"
        ));
        ROLE_SKILLS.put("Database Engineer", Arrays.asList(
            "sql", "mysql", "postgresql", "mongodb", "redis", "hibernate"
        ));
        ROLE_SKILLS.put("Android Developer", Arrays.asList(
            "java", "sql", "git", "rest api"
        ));
        ROLE_SKILLS.put("iOS Developer", Arrays.asList(
            "sql", "git", "rest api", "javascript"
        ));
        ROLE_SKILLS.put("Game Developer", Arrays.asList(
            "java", "python", "git", "data structures", "algorithms"
        ));
        ROLE_SKILLS.put("Server Side Game Developer", Arrays.asList(
            "java", "node.js", "rest api", "sql", "redis", "docker"
        ));
        ROLE_SKILLS.put("Blockchain Developer", Arrays.asList(
            "javascript", "node.js", "rest api", "docker", "python"
        ));
        ROLE_SKILLS.put("Software Architect", Arrays.asList(
            "java", "spring boot", "microservices", "rest api", "sql",
            "docker", "kubernetes", "system design"
        ));
        ROLE_SKILLS.put("QA Engineer", Arrays.asList(
            "java", "python", "git", "sql", "agile"
        ));
        ROLE_SKILLS.put("Technical Writer", Arrays.asList(
            "git", "agile", "communication"
        ));
        ROLE_SKILLS.put("UX Designer", Arrays.asList(
            "html", "css", "javascript", "communication"
        ));
        ROLE_SKILLS.put("Product Manager", Arrays.asList(
            "agile", "communication", "data analysis", "sql"
        ));
        ROLE_SKILLS.put("Engineering Manager", Arrays.asList(
            "agile", "communication", "system design", "git", "java"
        ));
        ROLE_SKILLS.put("Developer Relations", Arrays.asList(
            "javascript", "python", "git", "communication", "rest api"
        ));
    }

    // All known skills — used to extract extra keywords from a pasted JD
    private static final List<String> ALL_SKILLS = Arrays.asList(
        "java", "python", "node.js", "spring boot", "django", "rest api", "microservices",
        "react", "angular", "vue", "javascript", "html", "css", "typescript",
        "sql", "mysql", "postgresql", "mongodb", "redis", "hibernate",
        "docker", "kubernetes", "aws", "azure", "git", "jenkins", "linux",
        "machine learning", "deep learning", "tensorflow", "pandas", "numpy",
        "data analysis", "tableau",
        "data structures", "algorithms", "object oriented", "system design",
        "agile", "communication"
    );

    public ATSResult score(String resumeText, String role, String jobDescription) {
        String lower = resumeText.toLowerCase();

        // Build expected skill set: role skills + JD keywords
        Set<String> expected = new LinkedHashSet<>(
            ROLE_SKILLS.getOrDefault(role, Collections.emptyList())
        );
        if (jobDescription != null && !jobDescription.isBlank()) {
            String jdLower = jobDescription.toLowerCase();
            for (String skill : ALL_SKILLS) {
                if (jdLower.contains(skill)) expected.add(skill);
            }
        }
        if (expected.isEmpty()) expected.addAll(ALL_SKILLS.subList(0, 10));

        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for (String skill : expected) {
            if (lower.contains(skill)) matched.add(skill);
            else                       missing.add(skill);
        }

        int score = expected.isEmpty() ? 0 : (int) Math.round((double) matched.size() / expected.size() * 100);
        String grade = scoreToGrade(score,
            new int[]{80, 60, 40},
            new String[]{"Strong Match", "Good Match", "Partial Match", "Weak Match"});

        return new ATSResult(score, grade, matched, missing);
    }

    static String scoreToGrade(int score, int[] thresholds, String[] labels) {
        for (int i = 0; i < thresholds.length; i++) {
            if (score >= thresholds[i]) return labels[i];
        }
        return labels[labels.length - 1];
    }

    public static class ATSResult {
        public final int          score;
        public final String       grade;
        public final List<String> matched;
        public final List<String> missing;

        public ATSResult(int score, String grade, List<String> matched, List<String> missing) {
            this.score   = score;
            this.grade   = grade;
            this.matched = matched;
            this.missing = missing;
        }
    }
}
