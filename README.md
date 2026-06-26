# CPU Scheduler

A **native Android** app (Java + XML) that visualizes how five classic CPU
scheduling policies decide which process runs next. Edit a process set, pick an
algorithm, run it, and scrub an animated Gantt chart while per-process metrics
update tick by tick.

> **Fully offline, on-device.** The scheduling engine is plain Java running on
> the phone — no React Native, no server, no network. This project was ported
> from an earlier React Native prototype to 100% native Android.

| | |
|---|---|
| Language | **Java** (UI built in code + XML resources) |
| Min / target / compile SDK | 24 / 34 / 34 |
| Build | Gradle 8.8, Android Gradle Plugin 8.5.2, **JDK 17** |
| Dependencies | AndroidX AppCompat, Material 3, Lifecycle (ViewModel/LiveData), Fragment |
| Architecture | Single Activity + 4 Fragments, shared `ViewModel`, custom `View`s |

---

## Table of contents

- [Architecture](#architecture)
- [The scheduling engine](#the-scheduling-engine)
- [Features](#features)
- [Project layout](#project-layout)
- [Build &amp; run (Android Studio)](#build--run-android-studio)
- [Theming](#theming)
- [Design source](#design-source)

---

## Architecture

Everything runs locally; there is no client/server split.

```
MainActivity                       single activity: header + content + custom bottom nav
  ├─ HomeFragment                  pitch, CTA, feature cards, algorithm summary
  ├─ SimulatorFragment             process editor, controls, Gantt playback, metrics
  ├─ CompareFragment               every selected policy over the same set
  └─ LearnFragment                 expandable theory cards

SchedulerViewModel (activity-scoped, LiveData<SimState>)
  ├─ process set / algorithm / quantum / Compare selection
  ├─ run + playback model with a Handler-based ticker (620ms / speed)
  └─ emits immutable SimState snapshots to the fragments

engine/ (pure Java, no Android)        model/ (plain data classes)
  ├─ SchedulingEngine  simulate()/compare()   ├─ ProcessInput, ScheduleSegment
  ├─ Playback          derive() CPU/queue view ├─ ProcessMetrics, Averages
  ├─ Algorithm         the 5 policies          ├─ SimulationResult
  ├─ Presets           presets + process colors├─ PlaybackView
  └─ Theory            Learn content           └─ CompareEntry / CompareResponse

ui/ custom views
  ├─ GanttView      Canvas: bars, fog overlay, playhead, time axis
  ├─ MiniGanttView  Canvas: compact strip for Compare cards
  ├─ FlowLayout     wrapping chip/pill rows
  └─ Ui             dp/colour/typeface helpers + view factories (pills, chips, cards)
```

**Key points**

- **State lives in one place.** `SchedulerViewModel` is activity-scoped, so the
  Simulator and Compare screens share the same process set and quantum. It emits
  immutable `SimState` snapshots; fragments observe and render.
- **Playback** is a `Handler` ticker in the ViewModel that advances `currentTime`
  by one unit every `round(620 / speed)` ms and parks at the end — the same model
  as the original.
- **The engine is framework-free.** `engine/` and `model/` import nothing from
  Android, which is why they can be unit-checked with a plain `javac` run (see
  below) and would drop straight into a JVM test.
- **The charts are custom `View`s** that draw on a `Canvas` — the Gantt bars,
  process colors, dashed idle blocks, fog overlay, playhead, and boundary time
  axis are all drawn directly.

---

## The scheduling engine

Five policies, driven tick-by-tick over a working copy of the process set:

| Key | Algorithm | Preemptive | Selection rule (ties: arrival, then id) |
| --- | --- | --- | --- |
| `FCFS` | First Come First Serve | no | earliest arrival |
| `SJF` | Shortest Job First | no | smallest burst |
| `SRTF` | Shortest Remaining Time | yes | smallest remaining time |
| `RR` | Round Robin | yes | cyclic FIFO queue, fixed quantum |
| `PRI` | Priority (lower number = higher priority) | no | smallest priority value |

Per-process metrics:

```
turnaround = completion − arrival
waiting    = turnaround − burst
response   = firstStartTime − arrival
averages   = mean over all processes, rounded to 2 decimals
```

**Verified:** compiling `engine/` + `model/` with `javac` and running the Classic
preset reproduces the reference numbers exactly — FCFS → avg wait **4.75**, total
time **16**; across all policies **SRTF wins with avg wait 3.0** (SJF 4.0, RR 5.0,
PRI 5.5).

---

## Features

| Screen | What it does |
| --- | --- |
| **Home** | Pitch, primary CTA, two feature cards, and an at-a-glance algorithm summary |
| **Simulator** | Edit a process set (arrival / burst / priority), pick an algorithm, set the RR quantum, run it, and scrub the animated Gantt chart with CPU + ready-queue and per-process metrics (AT, BT, CT, TAT, WT, RT) |
| **Compare** | Select policies and run them over the same process set; lowest average wait is flagged `BEST WT` |
| **Learn** | Expandable theory cards (description + pro/con) for each policy |

Dark/light themes via an in-app toggle (defaults to dark).

---

## Project layout

```
CPUScheduling/
├── settings.gradle  build.gradle  gradle.properties   # root Gradle
├── gradlew  gradlew.bat  gradle/wrapper/              # Gradle 8.8 wrapper
├── app/
│   ├── build.gradle                                   # com.android.application
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/cpuscheduler/app/
│       │   ├── MainActivity.java  MainApp.java  ThemePref.java
│       │   ├── engine/    # SchedulingEngine, Playback, Algorithm, Presets, Theory
│       │   ├── model/     # immutable data types
│       │   ├── state/     # SchedulerViewModel, SimState
│       │   └── ui/        # fragments + GanttView / MiniGanttView / FlowLayout / Ui
│       └── res/
│           ├── layout/activity_main.xml
│           ├── drawable/  ic_nav_*.xml                # bottom-nav vector icons
│           ├── mipmap-*/  ic_launcher                 # launcher icons
│           ├── values/      colors, themes, bools, strings   (light)
│           └── values-night/ colors, bools                   (dark)
└── README.md
```

---

## Build &amp; run (Android Studio)

This is a standard Gradle Android project — open the **repo root** in Android
Studio.

**Prerequisites**

| Tool | Version |
| --- | --- |
| Android Studio | latest |
| JDK | **17** (Android Gradle Plugin 8.x requires it) |
| Android SDK Platform | 34 |
| Android SDK Build-Tools | 34.0.0 |

**Steps**

1. Android Studio → **Open** → select the repo root (`CPUScheduling`).
2. Let Gradle sync (first sync downloads AGP + AndroidX dependencies).
3. Ensure the Gradle JDK is **17** (*Settings → Build, Execution, Deployment →
   Build Tools → Gradle → Gradle JDK*).
4. Pick an emulator (API 24+) or a USB device and click ▶ **Run**.

Command line (with `ANDROID_HOME`/`local.properties` pointing at your SDK):

```bash
./gradlew assembleDebug      # → app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug       # build + install on a connected device/emulator
```

> No prebuilt APK ships in the repo — build one with the commands above. (The
> machine this was authored on had no Android SDK, so the engine was verified
> with `javac` but the APK must be built by you.)

---

## Theming

Colors are defined as semantic tokens in `res/values/colors.xml` (light) and
`res/values-night/colors.xml` (dark); the active variant resolves automatically.
The header toggle flips `AppCompatDelegate` night mode and persists the choice in
`SharedPreferences` (`ThemePref`), defaulting to **dark**. The accent cyan
`#22d3ee` and process palette are theme-invariant.

---

## Design source

This implementation recreates a Claude Design handoff (`CPU Scheduler.dc.html`).
That bundle is not checked into this repo — the app and the engine are the living
source of truth, and the reference numbers above (Classic preset: FCFS 4.75 / 16;
SRTF 3.0) pin the behavior to the original design.
