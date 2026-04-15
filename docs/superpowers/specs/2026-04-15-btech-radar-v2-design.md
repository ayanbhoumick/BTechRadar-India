# BTech Radar v2 — Design Spec
**Date:** 2026-04-15
**Status:** Approved

---

## Overview

This spec covers three changes to BTech Radar:
1. A new homepage with navigation to the Radar feature and a Resume Analyzer placeholder
2. A multi-API job aggregation backend (Adzuna + JSearch + CareerJet) with parallel fetch and deduplication
3. A full UI redesign — minimal, typographic, Geist Mono font, light/dark mode

---

## 1. Architecture & File Structure

### Approach
Shared layout shell (Approach B) — multiple HTML pages sharing a common CSS base. No SPA routing, no framework.

### New Frontend Files
```
src/main/resources/static/
├── index.html          — homepage (new)
├── radar.html          — current index.html renamed
├── resume.html         — resume analyzer placeholder (Coming Soon)
└── shared/
    └── layout.css      — shared typography scale, nav, base styles
```

### New Backend Files
```
service/
├── JSearchFetchService.java       — fetches from JSearch via RapidAPI
├── CareerJetFetchService.java     — fetches from CareerJet API
└── JobAggregatorService.java      — parallel fetch + merge + deduplication
```

### Modified Backend Files
- `SkillGapService.java` — inject `JobAggregatorService` instead of `JobFetchService`
- `CurriculumGapService.java` — inject `JobAggregatorService` instead of `JobFetchService`
- `JobFetchService.java` — becomes the Adzuna-only provider (no logic changes, just role narrows)

### Data Flow
```
Controller
  → JobAggregatorService
      → fetchFromAdzuna()     (existing JobFetchService)
      → fetchFromJSearch()    (new)
      → fetchFromCareerJet()  (new)
      [all three run via CompletableFuture in parallel]
  → merge all results
  → deduplicate by normalized (title + company)
  → existing SkillExtractorService pipeline (unchanged)
```

---

## 2. Homepage Design

A minimal landing page sharing the same visual language as the redesigned Radar page.

### Layout
- **Nav:** slim top bar — "BTech Radar" wordmark left, theme toggle right
- **Hero:** large left-aligned heading, short tagline below
- **Two cards side by side:**
  - **Radar** — "Analyse job market demand, skill gaps and curriculum relevance" → links to `radar.html`
  - **Analyse my Resume** — "Upload your resume and get a personalised skill report" → links to `resume.html`
- **Footer:** minimal — version tag only

### resume.html (placeholder)
Single centered "Coming Soon" message with a back link to homepage. No functionality.

---

## 3. Multi-API Backend

### APIs
| Provider | Coverage | Notes |
|----------|----------|-------|
| Adzuna | India tech jobs | Already integrated, `/in/` endpoint |
| JSearch (RapidAPI) | LinkedIn India, Indeed India, Glassdoor India | Best India tech coverage |
| CareerJet | India tech jobs, global aggregator | Free API, India + tech filters |

All three APIs are called with the same `role` and `city` parameters passed from the frontend.

### JobAggregatorService
- Uses `CompletableFuture.allOf()` to run all three fetches in parallel
- Each provider returns `List<JobListing>`
- All lists are merged into one pool
- Deduplication: normalize title and company to lowercase + trimmed, drop entries where title+company already seen
- Returns deduplicated `List<JobListing>` to the calling service

### Error Handling
- If any single provider fails, log the error and continue with results from the others
- If all providers fail, fall back to the existing mock data in `JobFetchService`

### API Keys Required
- `adzuna.app.id` / `adzuna.app.key` — already in `application.properties`
- `jsearch.api.key` — RapidAPI key, add to `application.properties`
- `careerjet.affiliate.id` — CareerJet affiliate ID, add to `application.properties`

---

## 4. UI Redesign

Applied to both `index.html` (homepage) and `radar.html`.

### Font
- **Geist Mono** — loaded via CDN (`https://fonts.googleapis.com/css2?family=Geist+Mono`)
- Applied globally via `font-family: 'Geist Mono', monospace`

### Color System
| Token | Light Mode | Dark Mode |
|-------|-----------|-----------|
| Background | `#ffffff` | `#0a0a0a` |
| Text primary | `#111111` | `#f0f0f0` |
| Text secondary | `#666666` | `#888888` |
| Border | `#e5e5e5` | `#1f1f1f` |
| Accent (red) | `#ff3333` | `#ff3333` |
| Card background | `#f9f9f9` | `#111111` |

Red is used sparingly: the "INDIA." wordmark, CTA buttons, and active state highlights only.

### Visual Style
- No glassmorphism — panels are simple bordered cards or borderless sections
- No background gradients — flat solid background color
- Lots of whitespace, left-aligned content blocks
- Typography scale reduced ~15% from current sizes (fixes 100% zoom issue)
- Numbered section labels retained: `01 / PARAMETERS`, `02 / INTELLIGENCE OUTPUT`

### Theme Toggle
- Kept in top-right corner on all pages
- Toggles `dark` class on `<html>` element
- Preference persisted in `localStorage`

### Shared Layout (`layout.css`)
Defines: CSS custom properties for the color tokens above, base font stack, typography scale, nav shell, card component, theme toggle button. Imported by all HTML pages.

---

## 5. Out of Scope (This Spec)
- Resume Analyzer feature (future spec)
- Vercel/Railway hosting setup (deferred)
- Reed API integration (dropped — UK-focused, not relevant for India)
- Internshala integration (no official API)
