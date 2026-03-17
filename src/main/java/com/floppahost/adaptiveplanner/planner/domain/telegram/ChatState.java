package com.floppahost.adaptiveplanner.planner.domain.telegram;

public enum ChatState {
    IDLE,
    WAITING_FOR_EVENT_NAME,
    WAITING_FOR_EVENT_DAY,
    WAITING_FOR_EVENT_START,
    WAITING_FOR_EVENT_END
}
