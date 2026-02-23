# Product Requirements Document (PRD)
# TG AI Planner — AI-Powered Study & Work Orchestrator

Version: 1.0  
Status: Draft  
Author: [Your Name]  
Date: [YYYY-MM-DD]

---

# 1. Product Overview

## 1.1 Vision

TG AI Planner is an AI-powered personal planning system designed to enforce consistent study and structured daily execution while combining:

- University studies
- Full-time job (or job search)
- Skill development (e.g., Java DSA for interviews)
- Personal tasks and obligations

The system transforms goals into executable daily timelines with cognitive constraints and behavioral enforcement.

Primary objective:
> Combine studies and full-time job in a sustainable, structured, and enforced way.

---

# 2. Problem Statement

The user:
- Has high workload (studies + job + preparation).
- Lacks strong self-organization.
- Struggles with laziness and inconsistent execution.
- Needs enforced structure, not just a task list.
- Wants AI-generated plans based on goals.

Current tools (Notion, Todoist, Google Calendar) are passive.
They do not:
- Enforce cognitive limits.
- Adapt weekly study deficits.
- Generate structured skill programs automatically.
- Convert goals into executable daily time blocks.

---

# 3. Target User

Primary User:
- University student transitioning to part-time studies.
- Working or planning to work full-time.
- Preparing for technical interviews.
- Needs structure, discipline, and automation.

Secondary Future Users:
- Ambitious students.
- Early-career engineers.
- People with high workload and low structure.

---

# 4. Product Scope

## 4.1 Core Scope (MVP)

- User profile with cognitive limits.
- Fixed weekly schedule (work/classes/meals).
- Study subjects with weekly targets.
- Exams with difficulty and importance.
- AI goal → structured learning program.
- Daily plan generation (timeline blocks).
- Weekly redistribution of missed study.
- Telegram bot adapter (initially).
- Later: REST API

---

# 5. Functional Requirements

---

# 5.1 User Management

### FR-1 Create User
- Create new user.
- UUID-based identity.
- Optional email.

### FR-2 User Profile
User profile defines planning constraints:
- Wake time
- Sleep time
- Minimum sleep hours
- Focus duration (e.g., 50 min)
- Break duration (e.g., 10 min)
- Max heavy blocks per day
- Max flexible planned minutes per day
- Weekly study target minutes
- Strict enforcement flag

---

# 5.2 Fixed Events (Non-negotiable blocks)

User can define:
- Recurring weekly events (weekday + time range)
- One-time events (datetime range)
- Event types:
  - CLASS
  - LECTURE
  - MEETING
  - MEAL
  - EXERCISE
  - COMMUTE
  - PERSONAL
  - OTEHR

System behavior:
- Fixed events become non-movable blocks in daily plan.
- They define free slots.

---

# 5.3 Subjects

Each subject includes:
- Name
- Weekly target minutes
- Priority (1–5)

Used for:
- Study distribution.
- Exam pressure calculation.

---

# 5.4 Exams

Each exam includes:
- Subject reference
- Date/time
- Difficulty (1–5)
- Importance (1–5)

System computes:

Exam Pressure = (difficulty × importance) / days_remaining

Pressure influences:
- Subject study allocation.
- Prioritization.

---

# 5.5 Tasks

Each task includes:
- Title
- Estimated minutes
- Priority
- Deadline (optional)
- Type (HOMEWORK, ASSIGNMENT, PROJECT, READING, PRACTICE, ADMIN, OTHER)

Tasks can be:
- Scheduled into blocks.
- Converted into timeline entries.

---

# 5.6 Programs (AI Generated Skill Plans)

User input:
"I want to improve in Java Data Structures and Algorithms for mid-level interview."

System must:
1. Use AI (Gemini) to generate:
    - 4–8 week structured program
    - Ordered program items
    - Estimated study minutes
2. Store as:
    - Program
    - ProgramItems
3. Automatically distribute into daily plans.

Program contains:
- Title
- Objective
- Weekly target minutes
- Priority
- Date range
- Items:
    - LEARN
    - PRACTICE
    - REVIEW
    - MOCK

---

# 5.7 Day Plan Generation (Core Engine)

Given:
- User profile
- Fixed events
- Subjects
- Exams
- Tasks
- Programs
- Target study minutes

System must:

1. Build daily time window (wake → sleep).
2. Expand fixed events into busy slots.
3. Compute free slots.
4. Allocate study blocks:
    - Respect focus + break durations.
    - Respect max heavy blocks per day.
    - Respect max flexible minutes per day.
5. Insert:
    - Fixed blocks
    - Study blocks
    - Break blocks
6. Validate:
    - No overlap.
    - Inside planning window.
    - Respect cognitive caps.

Output:
Ordered list of blocks forming daily timeline.

---

# 5.8 Block Types

Block kinds:
- WORK
- CLASS
- MEAL
- STUDY
- TASK
- PROGRAM
- BREAK
- OTHER

Block statuses:
- PLANNED
- IN_PROGRESS
- DONE
- SKIPPED
- SNOOZED

Each block may reference:
- FixedEvent
- Subject
- Exam
- Task
- Program
- ProgramItem

---

# 5.9 Weekly Redistribution

Goal:
Maintain weekly study consistency.

Process:
1. Calculate completed study minutes this week.
2. Compare to weekly target.
3. If deficit:
    - Redistribute remaining minutes across remaining days.
    - Regenerate daily plans.
4. Respect daily caps.

---

# 6. Non-Functional Requirements

## 6.1 Architecture

- Clean Architecture.
- Domain layer pure (no framework).
- Application layer with use cases.
- Infrastructure layer (DB, persistence).
- Presentation layer (Telegram, REST later).

## 6.2 Technology Stack (Initial)

Backend:
- Java 21
- Spring Boot 4.x
- Lombok
- PostgreSQL

AI:
- Gemini API

Presentation:
- Telegram Bot adapter

---

# 7. API Requirements

## Core Endpoints

- POST   /api/v1/users
- PUT    /api/v1/users/{id}/profile
- PUT    /api/v1/users/{id}/fixed-events
- POST   /api/v1/users/{id}/subjects
- POST   /api/v1/users/{id}/exams
- POST   /api/v1/users/{id}/tasks
- POST   /api/v1/users/{id}/programs/generate
- POST   /api/v1/users/{id}/plans/generate?date=YYYY-MM-DD
- GET    /api/v1/users/{id}/plans/{date}
- POST   /api/v1/users/{id}/blocks/{blockId}/action

---

# 8. AI Integration Requirements

Gemini must:

1. Convert goal → structured program.
2. Estimate realistic durations.
3. Break skills into phases.
4. Output JSON-compatible structure.

AI must NOT:
- Directly modify schedule.
- Bypass cognitive caps.

AI output always validated by domain.

---

# 9. Success Metrics

Short-term:
- Daily plans generated successfully.
- Weekly study target achieved.
- No schedule overlaps.

Mid-term:
- User maintains consistent study for 4+ weeks.
- Reduction in missed blocks.

Long-term:
- Interview readiness improvement.
- Academic performance stability.
- Sustainable work-study balance.

---

# 10. Future Enhancements

- Adaptive cognitive modeling.
- Fatigue tracking.
- Real-time Telegram nudging.
- Performance analytics dashboard.
- Habit consistency scoring.
- AI-driven schedule correction.
- Dynamic difficulty scaling.

---

# 11. Risks

- Overengineering early.
- AI overpromising unrealistic plans.
- User burnout due to strict caps.
- Scope creep.

Mitigation:
- MVP-first approach.
- Strong domain validation.
- Hard caps enforced.

---

# 12. Core Value Proposition

This is not a to-do app.

This is:

An AI-driven cognitive scheduler that enforces structured execution of high-pressure goals while balancing studies and full-time work.

---

# End of PRD