# Product Requirements Document (PRD)

## CPU Scheduling Simulator

| Document field | Details |
|---|---|
| Product name | CPU Scheduling Simulator |
| Product type | Native Android application |
| Technology | Java, XML, Android Studio |
| Package name | `com.example.cpuschedulingsimulator` |
| Project type | Operating Systems course project |
| Development target | Three-day Minimum Viable Product (MVP) |
| Document status | Version 1.0 |
| Last updated | June 22, 2026 |

---

## 1. Product Summary

CPU Scheduling Simulator is a beginner-friendly Android application that allows a user to select a CPU scheduling algorithm, enter process information, calculate the schedule, and view the results in a table and Gantt chart.

The application will support the following algorithms:

1. First Come First Served (FCFS)
2. Shortest Job First, non-preemptive (SJF)
3. Priority Scheduling, non-preemptive
4. Round Robin (RR)

The final application will be packaged as an Android APK so it can be installed and demonstrated on an Android phone.

---

## 2. Problem Statement

Students learning Operating Systems often find CPU scheduling algorithms difficult because they must manually track process arrival times, execution order, waiting times, turnaround times, and completion times.

A visual simulator can make these algorithms easier to understand by:

- Accepting process information from the user.
- Applying the selected scheduling algorithm.
- Showing the calculated values clearly.
- Displaying the execution order as a Gantt chart.

---

## 3. Product Goal

The goal is to build a simple, reliable Android application that correctly demonstrates four common CPU scheduling algorithms and produces an installable APK within three days.

### 3.1 Primary objective

Allow a user to enter a small set of processes and immediately see the schedule produced by a selected algorithm.

### 3.2 Learning objective

Keep the scheduling logic separate from the Android user interface so that the developer can understand, test, and explain both parts independently.

---

## 4. Target Users

The primary users are:

- Operating Systems students.
- Teachers demonstrating CPU scheduling.
- Beginners learning scheduling calculations.

The application is intended for educational use, not for controlling real operating-system processes.

---

## 5. MVP Scope

### 5.1 Features included

The three-day MVP will include:

- A home screen containing four scheduling algorithm options.
- An input screen for process data.
- Automatic process IDs such as P1, P2, and P3.
- Support for 1 to 10 processes per simulation.
- Validation of all required inputs.
- Correct calculation of scheduling results.
- A result table.
- Average waiting time.
- Average turnaround time.
- A horizontally scrollable Gantt chart.
- An installable debug APK.

### 5.2 Features excluded from the MVP

The following features are outside the three-day scope:

- User accounts or login.
- Database storage.
- Saving previous simulations.
- Cloud synchronization.
- Internet access.
- Exporting results to PDF or image.
- Comparing multiple algorithms on one screen.
- Preemptive SJF.
- Preemptive Priority Scheduling.
- Context-switch overhead calculations.
- Advanced animations.
- Dark mode customization.
- Tablet-specific layouts.

These may be added after the MVP is working correctly.

---

## 6. Success Criteria

The project will be considered successful when all of the following are true:

1. The application launches without crashing.
2. The user can select any of the four algorithms.
3. The user can enter valid process information.
4. Invalid input produces a clear message instead of a crash.
5. Each scheduling algorithm produces correct results for known test cases.
6. The result table shows the required calculated values.
7. The Gantt chart shows the correct execution order and time boundaries.
8. The user can return and perform another simulation.
9. An APK can be generated, installed, and opened on an Android phone.

---

## 7. Main User Flow

```text
Open application
       |
       v
Select an algorithm
       |
       v
Enter number of processes
       |
       v
Generate process input rows
       |
       v
Enter arrival time, burst time, and optional values
       |
       v
Press Calculate
       |
       v
Validate input
       |
       +---- Invalid input ----> Show error and remain on input screen
       |
       +---- Valid input ------> Run selected scheduler
                                      |
                                      v
                           Display table and Gantt chart
```

---

## 8. Application Screens

The MVP will use three Android activities.

### 8.1 Screen 1: Algorithm Selection

**Activity:** `MainActivity`

**Purpose:** Allow the user to choose a scheduling algorithm.

**Required UI components:**

- Application title: CPU Scheduling Simulator.
- Short instruction: Select an algorithm.
- FCFS button or card.
- SJF Non-Preemptive button or card.
- Priority Non-Preemptive button or card.
- Round Robin button or card.

**Behaviour:**

- Pressing an algorithm opens `ProcessInputActivity`.
- The selected algorithm name or key is passed to the next activity.

### 8.2 Screen 2: Process Input

**Activity:** `ProcessInputActivity`

**Purpose:** Collect the process data required by the selected algorithm.

**Common UI components:**

- Selected algorithm name.
- Number of processes field.
- Generate Rows button.
- Scrollable area containing process rows.
- Calculate button.
- Reset button, if time permits.

**Input row fields:**

- Process ID: automatically generated and read-only.
- Arrival Time.
- Burst Time.
- Priority, only for Priority Scheduling.

**Additional Round Robin field:**

- Time Quantum.

**Behaviour:**

1. The user enters a process count from 1 to 10.
2. The user presses Generate Rows.
3. The application creates one row per process.
4. The user enters the required values.
5. The user presses Calculate.
6. The application validates the fields.
7. The correct scheduler is called.
8. The result is sent to `ResultActivity`.

### 8.3 Screen 3: Results

**Activity:** `ResultActivity`

**Purpose:** Display all calculated scheduling information.

**Required UI components:**

- Selected algorithm name.
- Process result table.
- Average Waiting Time.
- Average Turnaround Time.
- Horizontally scrollable Gantt chart.
- Back or New Simulation button.

**Result table columns:**

| Column | Meaning |
|---|---|
| Process | Process ID |
| AT | Arrival Time |
| BT | Burst Time |
| Priority | Priority value; shown only when relevant |
| CT | Completion Time |
| TAT | Turnaround Time |
| WT | Waiting Time |

---

## 9. Functional Requirements

### FR-01: Display algorithms

The application shall display FCFS, SJF Non-Preemptive, Priority Non-Preemptive, and Round Robin on the home screen.

**Acceptance criteria:**

- All four options are visible.
- Selecting an option opens the process input screen.
- The input screen displays the selected algorithm name.

### FR-02: Accept the number of processes

The application shall allow the user to enter a process count between 1 and 10.

**Acceptance criteria:**

- Values from 1 through 10 are accepted.
- Zero, negative values, empty input, non-numeric input, and values above 10 are rejected.
- A helpful validation message is shown.

### FR-03: Generate process rows

The application shall generate one input row for every process.

**Acceptance criteria:**

- A process count of 3 generates P1, P2, and P3.
- Process IDs cannot be edited.
- Generating a new process count clears the previous generated rows.

### FR-04: Accept arrival times

The application shall accept a non-negative integer arrival time for every process.

**Acceptance criteria:**

- Arrival time 0 is accepted.
- Negative, empty, decimal, and non-numeric values are rejected.

### FR-05: Accept burst times

The application shall accept a positive integer burst time for every process.

**Acceptance criteria:**

- Burst times greater than 0 are accepted.
- Zero, negative, empty, decimal, and non-numeric values are rejected.

### FR-06: Accept priority values

For Priority Scheduling, the application shall accept a positive integer priority for every process.

**Priority rule:** A smaller number represents a higher priority. Priority 1 is higher than Priority 2.

**Acceptance criteria:**

- The priority field is shown only for Priority Scheduling.
- Positive integers are accepted.
- Zero, negative, empty, decimal, and non-numeric values are rejected.

### FR-07: Accept a Round Robin time quantum

For Round Robin, the application shall accept one positive integer time quantum.

**Acceptance criteria:**

- The quantum field is visible only for Round Robin.
- A value greater than 0 is accepted.
- Zero, negative, empty, decimal, and non-numeric values are rejected.

### FR-08: Execute FCFS

The application shall schedule processes by arrival time.

**Tie-breaking rule:** If two processes have the same arrival time, preserve their original input order.

**Acceptance criteria:**

- The earliest-arriving process executes first.
- Every process executes until completion.
- Idle CPU time is represented when no process has arrived.

### FR-09: Execute SJF Non-Preemptive

The application shall select the shortest burst-time process from the processes that have already arrived.

**Tie-breaking rules:**

1. Smaller burst time first.
2. If burst times are equal, earlier arrival time first.
3. If both are equal, preserve original input order.

**Acceptance criteria:**

- A running process is not interrupted.
- The next process is selected only after the current process finishes.
- Idle CPU time is supported.

### FR-10: Execute Priority Non-Preemptive

The application shall select the highest-priority process from the processes that have already arrived.

**Tie-breaking rules:**

1. Smaller priority number first.
2. If priorities are equal, earlier arrival time first.
3. If both are equal, preserve original input order.

**Acceptance criteria:**

- A running process is not interrupted.
- The next process is selected only after the current process finishes.
- Idle CPU time is supported.

### FR-11: Execute Round Robin

The application shall execute ready processes in queue order for at most one time quantum per turn.

**Rules:**

- Newly arrived processes are added to the ready queue.
- A process with remaining burst time is returned to the end of the queue.
- A process that finishes is not added again.
- Context-switch overhead is treated as zero.
- Idle CPU time is represented when the ready queue is empty.

**Acceptance criteria:**

- No execution block exceeds the time quantum unless the process finishes sooner.
- A process may appear in multiple Gantt blocks.
- Completion time is recorded when the remaining burst time reaches zero.

### FR-12: Calculate process metrics

The application shall calculate:

```text
Turnaround Time = Completion Time - Arrival Time
Waiting Time    = Turnaround Time - Burst Time
```

For non-preemptive algorithms:

```text
Start Time = max(Current Time, Arrival Time)
Completion Time = Start Time + Burst Time
```

**Acceptance criteria:**

- CT, TAT, and WT are shown for every process.
- Waiting time is never negative for valid scheduling results.

### FR-13: Calculate averages

The application shall calculate:

```text
Average Waiting Time = Sum of Waiting Times / Number of Processes
Average Turnaround Time = Sum of Turnaround Times / Number of Processes
```

**Acceptance criteria:**

- Both averages are shown to no more than two decimal places.
- Calculations use all entered processes.

### FR-14: Display the Gantt chart

The application shall display one visual block per execution interval.

Each block shall contain:

- Process ID, or IDLE for unused CPU time.
- Start time.
- End time.

**Acceptance criteria:**

- Blocks appear in chronological order.
- Time boundaries match scheduler output.
- The chart can scroll horizontally when it is wider than the screen.

### FR-15: Start another simulation

The user shall be able to return to the input or algorithm selection screen.

**Acceptance criteria:**

- Android Back navigation works without crashing.
- A new simulation does not reuse stale result data unintentionally.

---

## 10. Non-Functional Requirements

### NFR-01: Usability

- The app shall use clear labels and beginner-friendly language.
- Numeric fields shall use a numeric keyboard.
- Error messages shall explain what must be corrected.
- Important content shall remain accessible on small screens through scrolling.

### NFR-02: Reliability

- Invalid input shall not crash the application.
- The scheduler shall return deterministic results for the same input.
- The app shall handle processes that arrive after an idle period.

### NFR-03: Performance

- A simulation of up to 10 processes shall complete immediately on a typical Android device.
- No network connection shall be required.

### NFR-04: Maintainability

- Scheduling logic shall be placed in separate Java classes.
- Android activities shall not contain full scheduling implementations.
- Every scheduling algorithm shall implement a common scheduler contract where practical.
- Field and method names shall clearly describe their purpose.

### NFR-05: Compatibility

- The app shall use the existing project minimum SDK configuration.
- The generated APK shall install on at least one physical Android phone used for testing.

### NFR-06: Privacy and security

- The app shall not collect personal data.
- The app shall not require internet permission.
- The app shall not require user authentication.

---

## 11. Technical Architecture

### 11.1 Recommended project structure

```text
app/src/main/
├── java/com/example/cpuschedulingsimulator/
│   ├── MainActivity.java
│   ├── activity/
│   │   ├── ProcessInputActivity.java
│   │   └── ResultActivity.java
│   ├── model/
│   │   ├── CpuProcess.java
│   │   ├── GanttBlock.java
│   │   └── ScheduleResult.java
│   ├── scheduler/
│   │   ├── Scheduler.java
│   │   ├── FcfsScheduler.java
│   │   ├── SjfScheduler.java
│   │   ├── PriorityScheduler.java
│   │   └── RoundRobinScheduler.java
│   └── util/
│       └── InputValidator.java
│
└── res/layout/
    ├── activity_main.xml
    ├── activity_process_input.xml
    ├── activity_result.xml
    ├── item_process_input.xml
    ├── item_result_process.xml
    └── item_gantt_block.xml
```

### 11.2 Layer responsibilities

| Layer | Responsibility |
|---|---|
| XML layouts | Define how screens and reusable rows look |
| Activities | Read input, control navigation, call schedulers, and display output |
| Model classes | Store process, Gantt block, and result data |
| Scheduler classes | Perform scheduling calculations without depending on Android views |
| Utility classes | Validate and safely convert user input |

### 11.3 Data flow

```text
XML input fields
       |
       v
ProcessInputActivity
       |
       v
InputValidator
       |
       v
List<CpuProcess>
       |
       v
Selected Scheduler
       |
       v
ScheduleResult
       |
       v
ResultActivity and XML result views
```

---

## 12. Data Models

### 12.1 CpuProcess

Represents one process and its calculated values.

Suggested fields:

```java
String processId;
int arrivalTime;
int burstTime;
int priority;
int remainingTime;
int startTime;
int completionTime;
int turnaroundTime;
int waitingTime;
int inputOrder;
```

**Notes:**

- `priority` is used by Priority Scheduling.
- `remainingTime` is used by Round Robin.
- `inputOrder` allows consistent tie-breaking.
- A copy of the original input data should be used if scheduling would otherwise modify it unexpectedly.

### 12.2 GanttBlock

Represents one continuous execution interval.

Suggested fields:

```java
String processId;
int startTime;
int endTime;
```

An idle interval may use `processId = "IDLE"`.

### 12.3 ScheduleResult

Represents the complete scheduler output.

Suggested fields:

```java
List<CpuProcess> processes;
List<GanttBlock> ganttBlocks;
double averageWaitingTime;
double averageTurnaroundTime;
```

### 12.4 Scheduler interface

```java
public interface Scheduler {
    ScheduleResult schedule(List<CpuProcess> processes);
}
```

`RoundRobinScheduler` may receive the time quantum through its constructor:

```java
Scheduler scheduler = new RoundRobinScheduler(timeQuantum);
```

---

## 13. Navigation and Data Transfer

### 13.1 MainActivity to ProcessInputActivity

Pass a small algorithm key using an Intent extra.

Suggested values:

```text
FCFS
SJF
PRIORITY
ROUND_ROBIN
```

### 13.2 ProcessInputActivity to ResultActivity

Preferred beginner-friendly options, in order:

1. Make the model classes implement `Serializable` and pass `ScheduleResult` through the Intent for the small MVP dataset.
2. Alternatively, pass primitive arrays or an `ArrayList` of serializable objects.
3. Avoid adding a database or dependency-injection framework for this project.

---

## 14. Input Validation Rules

| Input | Valid value | Invalid examples |
|---|---|---|
| Process count | Integer from 1 to 10 | Empty, 0, -1, 11, text |
| Arrival time | Integer greater than or equal to 0 | Empty, -2, 1.5, text |
| Burst time | Integer greater than 0 | Empty, 0, -3, 2.5, text |
| Priority | Integer greater than 0 | Empty, 0, -1, decimal, text |
| Time quantum | Integer greater than 0 | Empty, 0, -1, decimal, text |

### Validation behaviour

- Highlight or focus the first invalid field.
- Display a short error message close to the field or using a Toast.
- Do not open the result screen until all values are valid.
- Catch numeric conversion errors instead of allowing the application to crash.

---

## 15. UI Design Guidelines

The MVP interface should prioritize clarity over visual complexity.

### General rules

- Use a `ScrollView` or `NestedScrollView` for long screens.
- Use consistent margins and spacing.
- Use readable text sizes.
- Use a prominent Calculate button.
- Keep button labels explicit.
- Display the selected algorithm at the top of the input and result screens.
- Use a `HorizontalScrollView` for wide tables or the Gantt chart.

### Suggested input row layout

```text
P1    Arrival: [ 0 ]    Burst: [ 5 ]
```

For Priority Scheduling:

```text
P1    Arrival: [ 0 ]    Burst: [ 5 ]    Priority: [ 1 ]
```

### Suggested result layout

```text
Process | AT | BT | CT | TAT | WT
P1      | 0  | 5  | 5  | 5   | 0
P2      | 1  | 3  | 8  | 7   | 4

Average Waiting Time: 2.00
Average Turnaround Time: 6.00

Gantt Chart
|    P1    |  P2  |
0          5      8
```

---

## 16. Test Plan

### 16.1 Unit tests for scheduler classes

Scheduling tests should run in `app/src/test/java/` without requiring an emulator.

Suggested test class names:

```text
FcfsSchedulerTest.java
SjfSchedulerTest.java
PrioritySchedulerTest.java
RoundRobinSchedulerTest.java
```

### 16.2 Required test scenarios

#### Test A: FCFS basic case

| Process | AT | BT |
|---|---:|---:|
| P1 | 0 | 5 |
| P2 | 1 | 3 |
| P3 | 2 | 2 |

Expected Gantt chart:

```text
0 -- P1 -- 5 -- P2 -- 8 -- P3 -- 10
```

Expected results:

| Process | CT | TAT | WT |
|---|---:|---:|---:|
| P1 | 5 | 5 | 0 |
| P2 | 8 | 7 | 4 |
| P3 | 10 | 8 | 6 |

#### Test B: CPU idle time

| Process | AT | BT |
|---|---:|---:|
| P1 | 3 | 2 |
| P2 | 6 | 1 |

Expected Gantt chart begins with an IDLE block from time 0 to 3.

#### Test C: Equal arrival times

Use multiple processes with the same arrival time and verify each algorithm's tie-breaking rules.

#### Test D: One process

Verify all algorithms work with a single process.

#### Test E: SJF selection

Use multiple available processes with different burst times and verify that the shortest one is selected.

#### Test F: Priority selection

Use multiple available processes with different priority numbers and verify that the smallest number is selected.

#### Test G: Round Robin repetition

Use processes with burst times larger than the quantum and verify that processes appear in multiple Gantt blocks.

#### Test H: Invalid input

Check empty fields, negative numbers, zero burst time, process count above 10, and zero quantum.

### 16.3 Device testing

Before submission:

- Run the app on the Android emulator.
- Test all four algorithms.
- Generate the APK.
- Transfer it to a physical Android phone.
- Install it.
- Open it and complete at least one simulation.
- Confirm that text, tables, buttons, and the Gantt chart fit or scroll correctly.

---

## 17. Three-Day Implementation Plan

### Day 1: Backend and FCFS foundation

**Goal:** Finish the architecture and a tested FCFS scheduler.

Tasks:

1. Add this PRD to the project repository.
2. Create the `model`, `scheduler`, `activity`, and `util` packages.
3. Create `CpuProcess`, `GanttBlock`, and `ScheduleResult`.
4. Create the `Scheduler` interface.
5. Implement `FcfsScheduler`.
6. Add idle-time handling.
7. Add FCFS unit tests.
8. Create the basic algorithm selection XML and `MainActivity` navigation.
9. Commit the working backend to GitHub.

**Day 1 completion requirement:**

A Java test must pass a list of processes to `FcfsScheduler` and receive a correct `ScheduleResult`.

### Day 2: Complete FCFS UI and add SJF/Priority

**Goal:** Complete one full end-to-end simulation, then reuse the design.

Tasks:

1. Build `ProcessInputActivity` and its XML layout.
2. Generate process input rows dynamically.
3. Implement `InputValidator`.
4. Connect FCFS input to the FCFS scheduler.
5. Build `ResultActivity`.
6. Display the result table.
7. Display the Gantt chart.
8. Implement and test `SjfScheduler`.
9. Implement and test `PriorityScheduler`.
10. Connect SJF and Priority Scheduling to the existing screens.
11. Commit the completed features to GitHub.

**Day 2 completion requirement:**

FCFS must work from algorithm selection through result display. SJF and Priority backend tests must pass.

### Day 3: Round Robin, full testing, and APK

**Goal:** Complete the final algorithm, stabilize the app, and produce the APK.

Tasks:

1. Implement `RoundRobinScheduler` using a ready queue.
2. Add the time quantum input.
3. Add Round Robin unit tests.
4. Connect Round Robin to the UI.
5. Test tie cases and idle periods.
6. Test all validation rules.
7. Improve spacing and scrolling only after calculations are correct.
8. Run the app on an emulator.
9. Generate the debug APK.
10. Install and test the APK on a physical phone.
11. Fix any blocking issue.
12. Make the final GitHub commit.

**Day 3 completion requirement:**

All four algorithms work in the app, and the APK installs and opens successfully on a phone.

---

## 18. Git Commit Plan

Recommended small commits:

```text
Add product requirements document
Create scheduling data models
Implement and test FCFS scheduler
Create algorithm selection screen
Add dynamic process input form
Display FCFS result table
Add Gantt chart display
Implement and test SJF scheduler
Implement and test priority scheduler
Implement and test Round Robin scheduler
Add input validation
Polish UI and generate APK
```

---

## 19. Risks and Mitigation

| Risk | Impact | Mitigation |
|---|---|---|
| Too much UI work before calculations are correct | High | Complete and test backend classes first |
| All algorithms placed inside one activity | High | Keep one scheduler class per algorithm |
| Round Robin queue logic takes longer than expected | High | Finish the other three algorithms first and implement RR with a simple Java queue |
| Dynamic process rows are difficult | Medium | Use a vertical LinearLayout inside a ScrollView instead of RecyclerView |
| Results do not fit on small screens | Medium | Use vertical and horizontal scrolling |
| Incorrect tie-breaking creates inconsistent answers | High | Follow the explicit rules in this PRD and add tests |
| App crashes on empty input | High | Validate before parsing and catch conversion errors |
| APK works only on emulator | High | Test on a physical phone before submission |
| Scope becomes too large | High | Do not add excluded features until the MVP is complete |

---

## 20. Definition of Done

A feature is considered complete only when:

- Its code compiles.
- Its main valid-input path works.
- Invalid input is handled where applicable.
- The scheduler output has been checked against a manual answer.
- The feature does not break previously completed algorithms.
- The change has been committed to GitHub.

The entire MVP is done when:

- All four algorithms are selectable.
- Process inputs can be entered.
- Results and Gantt charts are displayed correctly.
- Known test cases pass.
- Invalid input does not crash the app.
- The APK installs and runs on a physical Android phone.

---

## 21. APK Deliverable

For development and classroom demonstration, the debug APK is expected at a path similar to:

```text
app/build/outputs/apk/debug/app-debug.apk
```

The exact path may depend on the Android Studio and Gradle configuration.

The final submission package should contain:

- The Android Studio project.
- The GitHub repository link, if required.
- The generated APK.
- This PRD.
- A short README with setup and usage instructions, if time permits.

---

## 22. Future Enhancements

After the MVP is complete, possible improvements include:

- Preemptive SJF.
- Preemptive Priority Scheduling.
- Comparison of algorithms using the same process set.
- Response time calculation.
- Throughput and CPU utilization metrics.
- Saving simulation history.
- Exporting results.
- Animated Gantt chart execution.
- Improved Material Design components.
- Accessibility improvements.
- Automated Android UI tests.

---

## 23. Final Development Priority

The implementation priority is:

```text
Correct scheduling logic
        >
Input validation
        >
Complete user flow
        >
Readable results and Gantt chart
        >
Visual polish
```

The project must remain small enough to finish within three days. Any optional feature should be postponed until the four required algorithms, result table, Gantt chart, and APK are working correctly.
