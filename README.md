# CPU Scheduling Simulator

An Android application that demonstrates common CPU scheduling algorithms used in operating systems. Enter process information, select an algorithm, and visualize the scheduling result with a table and Gantt chart.

## Algorithms

| Algorithm | Type | Description |
|---|---|---|
| FCFS | Non-preemptive | Executes processes in arrival order |
| SJF | Non-preemptive | Selects the shortest available burst time |
| SRTF | Preemptive | Preempts when a shorter job arrives |
| Priority | Non-preemptive | Runs the highest-priority available process |
| Preemptive Priority | Preemptive | Preempts when a higher-priority process arrives |
| Round Robin | Preemptive | Shares CPU using a fixed time quantum |

## Screens

1. **Algorithm Selection** — Choose from 6 scheduling algorithms
2. **Process Input** — Enter arrival time, burst time, and optional priority/quantum
3. **Results** — View completion table, averages, and Gantt chart

## Architecture

```
app/src/main/java/com/example/cpuschedulingsimulator/
├── MainActivity.java                  # Algorithm selection screen
├── activity/
│   ├── ProcessInputActivity.java      # Process input screen
│   └── ResultActivity.java            # Result display screen
├── model/
│   ├── CpuProcess.java                # Process data model
│   ├── GanttBlock.java                # Gantt chart block
│   └── ScheduleResult.java            # Scheduling result container
└── scheduler/
    ├── Scheduler.java                 # Algorithm interface
    ├── FcfsScheduler.java
    ├── SjfScheduler.java
    ├── SrtfScheduler.java
    ├── PriorityScheduler.java
    ├── PreemptivePriorityScheduler.java
    ├── RoundRobinScheduler.java
    └── util/
        ├── SchedulerUtils.java        # Shared helpers
        ├── TieBreaker.java            # Tie-breaking logic
        ├── ProcessMetrics.java        # CT/TAT/WT calculation
        ├── IdleCpuHelper.java         # CPU idle handling
        └── GanttChartBuilder.java     # Gantt block merging
```

## Testing

88 unit tests across 13 test classes covering all 6 algorithms and the shared utility layer.

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat testDebugUnitTest
```

## Build

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Tech Stack

- Java 11
- Android SDK (minSdk 24, targetSdk 36)
- JUnit 4
- Material Design Components
- Gradle 9.4.1
