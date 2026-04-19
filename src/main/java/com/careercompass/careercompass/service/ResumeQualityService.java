package com.careercompass.careercompass.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class ResumeQualityService {

    private static final List<String> ACTION_VERBS = Arrays.asList(
        "built", "designed", "developed", "led", "optimised", "optimized",
        "deployed", "implemented", "created", "managed", "improved",
        "architected", "automated", "reduced", "increased", "delivered",
        "launched", "collaborated", "integrated", "migrated"
    );

    private static final List<String> SECTION_HEADERS = Arrays.asList(
        "experience", "education", "skills", "projects"
    );

    private static final Pattern EMAIL_PATTERN   = Pattern.compile("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}");
    private static final Pattern PHONE_PATTERN   = Pattern.compile("(\\+91[\\s\\-]?)?[6-9]\\d{9}");
    private static final Pattern BULLET_PATTERN  = Pattern.compile("^[\\s]*[\\-•*]", Pattern.MULTILINE);
    private static final Pattern QUANT_PATTERN   = Pattern.compile("\\d+\\s*(%|percent|users|projects|years|months|systems|services|team)");

    public QualityResult assess(String text, int pageCount) {
        String lower = text.toLowerCase();
        List<String> issues      = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();
        int          passed      = 0;
        int          total       = 7;

        // 1. Word count
        int words = text.trim().split("\\s+").length;
        if (words >= 300 && words <= 1000) {
            passed++;
        } else if (words < 300) {
            issues.add("Resume too short (" + words + " words) — aim for 350–900");
            suggestions.add("Expand project descriptions and experience with measurable details");
        } else {
            issues.add("Resume too long (" + words + " words) — trim to 900 or under");
            suggestions.add("Cut filler sentences; each bullet should justify its presence");
        }

        // 2. Page count
        if (pageCount >= 1 && pageCount <= 2) {
            passed++;
        } else {
            issues.add("Resume is " + pageCount + " pages — keep to 1–2 pages");
            suggestions.add("Condense older or less relevant experience");
        }

        // 3. Contact info
        boolean hasEmail = EMAIL_PATTERN.matcher(text).find();
        boolean hasPhone = PHONE_PATTERN.matcher(text).find();
        if (hasEmail && hasPhone) {
            passed++;
        } else {
            if (!hasEmail) { issues.add("No email address detected"); suggestions.add("Add a professional email address"); }
            if (!hasPhone) { issues.add("No phone number detected"); suggestions.add("Add an Indian mobile number (+91 format preferred)"); }
        }

        // 4. Section headers
        List<String> missingSections = new ArrayList<>();
        for (String header : SECTION_HEADERS) {
            if (!lower.contains(header)) missingSections.add(header.toUpperCase());
        }
        if (missingSections.isEmpty()) {
            passed++;
        } else {
            issues.add("Missing sections: " + String.join(", ", missingSections));
            suggestions.add("Add clearly labelled " + String.join(", ", missingSections) + " sections");
        }

        // 5. Bullet density
        long bulletCount = BULLET_PATTERN.matcher(text).results().count();
        if (bulletCount >= 5) {
            passed++;
        } else {
            issues.add("Only " + bulletCount + " bullet points found — use bullets for responsibilities and achievements");
            suggestions.add("Reformat experience and projects as bullet points");
        }

        // 6. Quantification
        long quantCount = QUANT_PATTERN.matcher(lower).results().count();
        if (quantCount >= 2) {
            passed++;
        } else {
            issues.add("Few quantified achievements (" + quantCount + " found) — add numbers, percentages, or scale");
            suggestions.add("Add metrics: 'Reduced load time by 40%', 'Built for 500+ users', etc.");
        }

        // 7. Action verbs
        long verbCount = ACTION_VERBS.stream().filter(lower::contains).count();
        if (verbCount >= 4) {
            passed++;
        } else {
            issues.add("Only " + verbCount + " action verbs found — use strong verbs to start bullets");
            suggestions.add("Start bullets with: Built, Designed, Deployed, Implemented, Led, Automated, etc.");
        }

        int    score = (int) Math.round((double) passed / total * 100);
        String grade = ATSScoringService.scoreToGrade(score,
            new int[]{80, 60, 40},
            new String[]{"Well Formatted", "Decent", "Needs Work", "Poor"});

        return new QualityResult(score, grade, issues, suggestions, words);
    }

    public static class QualityResult {
        public final int          score;
        public final String       grade;
        public final List<String> issues;
        public final List<String> suggestions;
        public final int          wordCount;

        public QualityResult(int score, String grade, List<String> issues, List<String> suggestions, int wordCount) {
            this.score       = score;
            this.grade       = grade;
            this.issues      = issues;
            this.suggestions = suggestions;
            this.wordCount   = wordCount;
        }
    }
}
