package com.floppahost.adaptiveplanner.planner.domain.planning;

import java.util.Objects;
import java.util.UUID;

public sealed interface BlockRef {

    record Empty() implements BlockRef {}

    record ForEvent(UUID eventId) implements BlockRef {
        public ForEvent {
            Objects.requireNonNull(eventId, "Event ID cannot be null");
        }
    }

    record ForSubject(UUID subjectId) implements BlockRef {
        public ForSubject {
            Objects.requireNonNull(subjectId, "Subject ID cannot be null");
        }
    }

    record ForExam(UUID examId) implements BlockRef {
        public ForExam {
            Objects.requireNonNull(examId, "Exam ID cannot be null");
        }
    }

    record ForTask(UUID taskId) implements BlockRef {
        public ForTask {
            Objects.requireNonNull(taskId, "Task ID cannot be null");
        }
    }

    record ForProgram(UUID programId, UUID programItemId) implements BlockRef {
        public ForProgram {
            Objects.requireNonNull(programId, "Program ID cannot be null");
            Objects.requireNonNull(programItemId, "Program Item ID cannot be null");
        }
    }
}
