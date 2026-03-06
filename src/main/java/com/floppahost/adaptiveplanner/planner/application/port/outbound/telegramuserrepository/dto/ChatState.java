package com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserrepository.dto;

public enum ChatState {
    IDLE,
    WAITING_FOR_JOB_DAY,
    WAITING_FOR_JOB_START_TIME,
    WAITING_FOR_JOB_END_TIME,
    WAITING_FOR_UNI_CLASS_NAME
}
