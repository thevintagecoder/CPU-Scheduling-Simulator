package com.example.cpuschedulingsimulator.scheduler;

import com.example.cpuschedulingsimulator.model.CpuProcess;
import com.example.cpuschedulingsimulator.model.ScheduleResult;

import java.util.List;

/**
 * Every scheduling algorithm must implement this interface.
 */
public interface Scheduler {

    ScheduleResult schedule(List<CpuProcess> processes);
}
