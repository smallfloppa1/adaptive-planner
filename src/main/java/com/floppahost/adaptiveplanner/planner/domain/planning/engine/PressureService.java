package com.floppahost.adaptiveplanner.planner.domain.planning.engine;


import com.floppahost.adaptiveplanner.planner.domain.academic.Exam;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Calculates academic pressure based on upcoming exams.
 * Pressure formula: (difficulty * importance) / max(days_left, 1)
 */
public class PressureService {

    public double calculateExamPressure(LocalDate today, Exam exam) {
        LocalDate examDate = exam.getStartsAt().toLocalDate();
        
        long daysLeft = ChronoUnit.DAYS.between(today, examDate);
        
        if (daysLeft < 0) {
            return 0.0;
        }
        
        return (exam.getDifficulty() * exam.getImportance()) / (double) Math.max(daysLeft, 1);
    }

    /**
     * Build a map of subject pressure values by aggregating exam pressures.
     * Only includes upcoming exams with positive pressure.
     */
    public Map<UUID, Double> buildSubjectPressure(LocalDate today, List<Exam> exams) {
        Map<UUID, Double> pressures = new HashMap<>();
        
        for (Exam exam : exams) {
            double pressure = calculateExamPressure(today, exam);
            
            if (pressure > 0) {
                pressures.merge(exam.getSubjectId(), pressure, Double::sum);
            }
        }
        
        return pressures;
    }
}
