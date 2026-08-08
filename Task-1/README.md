# SyncTask — Senior-Level Android Task Management

**SyncTask** is a production-grade To-Do & Task Management application built around **Clean Architecture**, **MVVM**, and **Modern Android Development (MAD)**. It's been upgraded from an intern scaffold into a portfolio-ready showcase with professional patterns, delightful micro-interactions, and a custom-tailored design language that avoids generic "AI-generated" aesthetics.

---

## ✨ Key Features

### 🏗 Architecture & Engineering
- **Clean Architecture**: Strict separation of Domain, Data, and Presentation layers. All features start as UseCases in the Domain.
- **Dagger Hilt DI**: Full migration from manual providers to `@HiltAndroidApp`, singleton modules for Room, repositories, and use cases.
- **Edge-to-Edge UI**: Transparent system bars with proper inset handling (`safeDrawing`, `navigationBarsPadding`) for a truly immersive canvas.
- **Room Persistence**: Versioned schema, converters for enums, reactive Flow queries.

### 🤌 Micro-Interactions & Motion
- **Haptic Feedback**: `LocalHapticFeedback` fires on every toggle, swipe, delete, FAB tap, and sheet confirmation — calibrated to feel "meaty" not annoying.
- **Confetti Celebration**: Custom Compose-Canvas particle burst (circle/rect/triangle shapes, gravity, rotation, drift + a scaled check badge) when a **High Priority** task is marked Done.
- **Dual-Direction Swipe**:
  - **Swipe Right →** Green secondary container toggles completion.
  - **Swipe Left →** Red error container deletes the task.
  - Both sides scale in during progress and trigger haptics at threshold.
- **Animated Checkbox**: Custom Canvas-drawn check with stroke-grow animation and gradient container swap.
- **Smart Due Dates**: "Today / Tomorrow / Overdue" formatting with a dedicated overdue pill & error tint.

### 🔔 Senior Features
- **Task Reminders with WorkManager**: Pick any date + time via native `DatePickerDialog` + `TimePickerDialog`. `ReminderManager` enqueues a unique `OneTimeWorkRequest` per task. When it fires, `TaskReminderWorker` builds a priority `NotificationCompat` channel with vibration + sound + deep-link `PendingIntent` back into `MainActivity`.
- **Task Analytics**:
  - **Swipe-in stats card** on the home screen with a gradient **sweep-gradient Canvas progress ring** and animated percentage counter.
  - Expandable **Stats Bottom Sheet** with priority-breakdown progress bars (High/Medium/Low), done/remaining counters, and the full-detail progress ring.
- **Overdue Detection**: Live overdue badge appears next to past-due pending tasks.

### 🎨 Polish
- **Shimmer Loading**: Custom `LinearGradient` shimmer with 1200 ms `FastOutSlowIn` sweep replaces the generic `CircularProgressIndicator`. Simulates both the stats card and 5 task skeletons during cold load.
- **Empty State**: Custom Canvas illustration with:
  - Two orbital dashed rings animated in counter-rotating directions.
  - Central radial-gradient glow.
  - Floating + pulsing `AddTask` icon.
  - Hand-crafted copy: "Your task canvas is blank."
- **Search Empty State**: Dedicated illustration when filters/search produce zero hits.
- **Custom Material 3 Color System**: Hand-picked Indigo/Emerald/Pink palette with every semantic token populated (`surfaceContainerLow/High/Highest`, `inverseSurface`, `scrim`, …).
- **Custom Typography Scale**: Full M3 typography with precise line-height, letter-spacing, and weight hierarchy.
- **Completion + Status Filters**: Dual chip rows (priority dots + icons for pending/done/all).
- **Edit Mode Flow**: Tap any task card → the bottom sheet opens prefilled for editing → `UpdateTaskUseCase` saves and `ReminderManager` reschedules the work.
- **Full Search & Filter Backward Compatibility**: Original search/filter logic preserved + improved sorting (pending first → priority desc → created desc).

---

## 🛠 Tech Stack
| Area | Library |
|---|---|
| **Language** | Kotlin 2.0.21 |
| **UI** | Jetpack Compose + Material 3 (custom tokens) |
| **Async** | Coroutines + Flows |
| **DI** | Dagger Hilt 2.51.1 |
| **DB** | Room 2.6.1 + KSP |
| **Processing** | KSP (Room + Hilt Compilers) |
| **Reminders** | WorkManager 2.9.1 + `NotificationCompat` |
| **Motion** | Compose Animation Core, `InfiniteTransition` |
| **Drawing** | Compose Canvas (`drawArc`, `drawPath`, particles) |
| **Build** | Gradle 9.1 (Kotlin DSL) + Compose BOM 2024.06 |

---

## 🏗 Project Structure
```text
com.syntecxhub.taskmanagement
│
├── SyncTaskApp.kt              ← @HiltAndroidApp Application class
│
├── di/                         ← Hilt modules
│   ├── DatabaseModule.kt
│   ├── RepositoryModule.kt
│   └── UseCaseModule.kt
│
├── data/                       ← Data Layer
│   ├── local/
│   │   ├── TaskDatabase.kt     (Room v2, TypeConverters)
│   │   ├── TaskDao.kt          (count queries + update op)
│   │   ├── TaskEntity.kt       (+reminderEnabled field)
│   │   └── Converters.kt
│   ├── mapper/TaskMapper.kt
│   ├── repository/TaskRepositoryImpl.kt (+ stats)
│   ├── manager/ReminderManager.kt
│   └── worker/TaskReminderWorker.kt (@HiltWorker)
│
├── domain/                     ← Domain Layer (pure Kotlin)
│   ├── model/
│   │   ├── Task.kt (+ reminderEnabled)
│   │   ├── Priority.kt
│   │   └── TaskStats.kt
│   ├── repository/TaskRepository.kt (+ updateTask, getTaskStats)
│   └── usecase/
│       ├── GetTasksUseCase.kt
│       ├── AddTaskUseCase.kt       (now returns Long)
│       ├── DeleteTaskUseCase.kt
│       ├── ToggleTaskCompletionUseCase.kt
│       ├── UpdateTaskUseCase.kt
│       └── GetTaskStatsUseCase.kt
│
├── presentation/
│   ├── state/
│   │   ├── TaskUiState.kt       (+ stats, editingTask, celebratedTaskId, …)
│   │   └── TaskUiEvent.kt       (+ AddTask fields, UpdateTask, Edit, Celebration, Notification)
│   ├── viewmodel/TaskViewModel.kt  (@HiltViewModel, full reminder integration)
│   └── ui/
│       ├── theme/Theme.kt       (full M3 color tokens + custom typography + SideEffect insets)
│       ├── screens/TaskListScreen.kt  (Scaffold, CelebrationBurst, sheets, animations)
│       └── components/
│           ├── TaskItem.kt              (SwipeToDismissBox L+R, checkbox canvas, overdue, haptic)
│           ├── AddTaskBottomSheet.kt    (DatePickerDialog + TimePickerDialog + reminder Switch + edit mode)
│           ├── PriorityBadge.kt         (pulse dot, gradient chips)
│           ├── StatsProgressRing.kt     (Canvas sweep gradient ring + MiniStatPill/Dot)
│           ├── ShimmerLoading.kt        (stats + 5 skeletons)
│           ├── EmptyState.kt            (+ SearchEmptyState, orbital illustration)
│           └── CelebrationBurst.kt      (custom particles + check badge)
│
└── MainActivity.kt              (@AndroidEntryPoint, edge-to-edge, POST_NOTIFICATIONS permission)
```

---

## 👨‍💻 Installation
1. Clone the repo.
2. Open in **Android Studio Ladybug+**.
3. Build → Run. All dependencies resolve from Maven Central (Custom Canvas Celebration implemented).
4. On Android 13+: grant **Notifications** when prompted to receive reminders.

---

## 📱 Design Philosophy
SyncTask intentionally rejects the generic "AI-looking" grid-of-cards aesthetic. Instead, you'll find:
- **Asymmetric hierarchy**: Large TopAppBar with subtitle → compact search → dual chip rows → stats card that sits between header and list.
- **Color "breathing"**: Priority badges pulse, progress rings sweep, high-priority tasks cast a real shadow until they're checked.
- **Micro-copy with voice**: Subtitles like "Your productivity companion", empty states like "Craft your first task and orchestrate your day with intention."
- **Physicality in motion**: Every haptic is matched by a visible scale/shrink — interactions feel *weighted*.
- **No generic icon-only FABs**: The FAB is rounded-square 20dp, slightly sized-up, plus bottom-nav-bar-padded so it never collides with 3-button gesture nav.

---

Built with ❤️ by Aman Kanojiya for Syntecxhub — upgraded to Senior Portfolio level.
