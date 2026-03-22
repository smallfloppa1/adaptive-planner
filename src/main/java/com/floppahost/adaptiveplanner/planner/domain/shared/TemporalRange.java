package com.floppahost.adaptiveplanner.planner.domain.shared;

import java.time.temporal.Temporal;

public interface TemporalRange<T extends Temporal> {

    T getStart();
    T getEnd();
    boolean isWithin(TemporalRange<T> range);
}
