# WGM Quiz — Who's Gonna Be Millionaire 🏆

A modern, high-production Android quiz application inspired by the iconic *Kaun Banega Crorepati / Who Wants to Be a Millionaire* game show format. Built with **100% Jetpack Compose**, **Kotlin Coroutines**, **Room Database**, and **Clean Architecture (MVVM + UDF)**.

---

## 🌟 Highlights & Features

### 1. 🎨 Home Screen Design
- **Full-screen immersive entry**: Deep dark navy-to-midnight-blue vertical gradient background with animated radial spotlight rays.
- **Animated Millionaire Logo**: The iconic circular "Who's Gonna Be Millionaire" badge displayed at 260dp with a pulsing gold halo (animated `glowAlpha`).
- **Tagline Card**: Semi-transparent dark purple card displaying *"One Question. / One Step Closer to a Million."*
- **Gold START QUIZ Button**: Horizontal gold gradient pill (`#E6A800 → #FFC107`) with a circular ▶ play icon and subtle breathing pulse animation.
- Clean, minimal layout — no distracting stats, nav bars, or secondary panels on the home screen.

### 2. 🎮 Core Game Engine & Mechanics
- **Dynamic Prize Progression**: Progressive 16-level money ladder from **₹1,000** up to the grand jackpot of **₹7 Crores**.
- **Safe-Haven Milestones**: Checkpoint thresholds securing earned cash even on a wrong answer:
  - **Tier 1**: Level 5 (₹10,000)
  - **Tier 2**: Level 10 (₹3,20,000)
  - **Jackpot**: Level 16 (₹7,00,00,000)
- **30-Second Active Countdown**: Circular countdown timer with real-time arc animation and urgency colour transitions (Gold → Orange → Red).
- **Persistent Economy & Scoring**: Tracks coins earned, high scores, games played, and best levels saved locally across sessions via SharedPreferences.

### 3. 📝 Question Bank — 45 Real Questions, 15 Difficulty Tiers
The question engine has been fully rewritten with **45 hand-crafted, real-world questions** (3 per difficulty level), covering:

| Levels | Category | Examples |
|--------|----------|---------|
| 1–3 | Easy: Pop culture, everyday life, India basics | Leap year days, Twitter characters, India's capital |
| 4–6 | Medium-Easy: Sports, geography, history | FIFA World Cup, Nile River, India's independence year |
| 7–9 | Medium: Science, tech, Indian culture | Android creator, speed of light, 'Jana Gana Mana' author |
| 10–12 | Hard: World history, advanced science, mathematics | Treaty of Versailles, Euler's number, atomic number of Gold |
| 13–15 | Expert: Deep Indian history, computer science, quantum physics | Battle of Plassey, O(n log n), Riemann Hypothesis |

### 4. 🛟 Complete 4-Lifeline System
- **50:50 (`✂️`)**: Eliminates two incorrect choices with smooth visual transition.
- **Audience Poll (`📊`)**: Custom modal dialog with difficulty-weighted probability distributions.
- **Flip the Question (`🔄`)**: Swaps the current question with a fresh alternative of the same difficulty.
- **Extra Life (`❤️`)**: Pop-up lifeline modal on incorrect answer granting a second chance.

### 5. 🔊 Audio System — Dual MediaPlayer Architecture
All 8 audio assets are correctly mapped to their intended game events:

| Asset | Engine | Trigger |
|-------|--------|---------|
| `Question.mp3` | **MediaPlayer (looping)** | Plays as sustained background the moment a question appears |
| `Timer.mp3` | **MediaPlayer (looping)** | Replaces Question.mp3 when the 30s countdown begins |
| `Lock.mp3` | SoundPool (one-shot) | On answer selection tap |
| `Right Answer.mp3` | SoundPool (one-shot) | Correct answer revealed |
| `Wrong Answer.mp3` | SoundPool (one-shot) | Wrong answer revealed |
| `Time Up.mp3` | SoundPool (one-shot) | Timer reaches zero |
| `Timer.mp3` | MediaPlayer | Countdown loop |

**Audio transition flow**: Question appears → `Question.mp3` loops → `stopQuestionBg()` called → `Timer.mp3` starts → answer selected → timer stops → result SFX plays.

- **Lifecycle-Aware**: Gracefully pauses/resumes all active MediaPlayers and SoundPool on Activity `onPause`/`onResume`.

---

## 🏛️ Architecture & Tech Stack

Strictly follows **Clean Architecture** with **Unidirectional Data Flow (UDF)**:

```
app/
 ├── src/main/
 │    ├── assets/                    # Audio SFX (8 files) & Millionaire.png logo
 │    ├── java/com/wgm/quiz/
 │    │    ├── audio/
 │    │    │    ├── WgmSoundManager.kt   # Dual MediaPlayer + SoundPool engine
 │    │    │    └── WgmSynthesizer.kt    # Pure Kotlin AudioTrack sine-wave generator
 │    │    ├── data/
 │    │    │    ├── local/               # Room DB (v2), DAO, Entities, ScoreRepository
 │    │    │    └── repository/          # RepositoryImpl — 45-question seed data
 │    │    ├── domain/
 │    │    │    ├── model/               # WgmQuestion domain model
 │    │    │    └── repository/          # WgmQuizRepository interface
 │    │    ├── ui/
 │    │    │    ├── components/          # Hexagon cards, Timer, Dialogs, Badges
 │    │    │    ├── screens/             # WgmHomeScreen, WgmGameScreen, MoneyLadder
 │    │    │    └── theme/               # Color tokens, gradients, shapes
 │    │    ├── viewmodel/                # MVVM StateFlow UDF game state machine
 │    │    ├── MainActivity.kt
 │    │    ├── SplashActivity.kt
 │    │    └── WgmApplication.kt
 │    └── res/
 ├── build.gradle
 └── .gitignore
```

### Key Technologies
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Clean Architecture + UDF StateFlow
- **Database**: Room v2 (with `fallbackToDestructiveMigration`)
- **Persistence**: SharedPreferences (score & coin economy)
- **Audio Engine**: Dual MediaPlayer (Question bg + Timer loop) + SoundPool (5 SFX)
- **Target SDK**: Android 14 (API Level 34)
- **Min SDK**: Android 7.0 (API Level 24)

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Hedgehog (2023.1.1) or newer
- **JDK**: Java Development Kit 17
- **Android SDK**: API Level 34

### Building & Running
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/codedbyamankanojiya/Syntecxhub-Tasks/Task-2.git
   cd Task-2
   ```
2. **Open in Android Studio** and navigate to the `Task-2` directory.
3. **Configure JDK 17** via `Settings → Build, Execution, Deployment → Build Tools → Gradle`.
4. **Build & Run** on your target device / emulator (**Shift + F10**).

> **Note**: Room DB is at version 2 with `fallbackToDestructiveMigration()`. On first launch (or upgrade from v1), the database is wiped and all 45 real questions are seeded automatically.

---

## 🔒 Confidentiality & Repository Cleanliness
- All build outputs (`build/`, `.gradle/`), machine-specific SDK files (`local.properties`), IDE workspace caches (`.idea/`), signing credentials, and keys are filtered via `.gitignore`.


---

## 🌟 Highlights & Features

### 1. 🎮 Core Game Engine & Mechanics
- **Dynamic Prize Progression**: Progressive 16-level money ladder ranging from **₹1,000** up to the grand jackpot of **₹7 Crores**.
- **Safe-Haven Milestones**: Checkpoint thresholds securing earned cash even on a wrong answer:
  - **Tier 1**: Level 5 (₹10,000)
  - **Tier 2**: Level 10 (₹3,20,000)
  - **Jackpot**: Level 16 (₹7,00,00,000)
- **30-Second Active Countdown**: Circular countdown timer with real-time arc animation, urgency pulse triggers, and color transitions (Gold → Orange → Red).
- **Persistent Economy & Scoring**: Tracks coins earned, high scores, games played, and best levels saved locally across sessions.

### 2. 🛟 Complete 4-Lifeline System
- **50:50 (`✂️`)**: Eliminates two incorrect choices with smooth visual transition.
- **Audience Poll (`📊`)**: Displays a custom modal dialog with difficulty-weighted probability distributions across options.
- **Flip the Question (`🔄`)**: Swaps out the current question with a fresh alternative of the same difficulty.
- **Extra Life (`❤️`)**: Pop-up lifeline modal on an incorrect answer granting a second chance at the current question level.
- **Inventory Badges**: Interactive bottom status indicators displaying real-time availability and usage state for each lifeline.

### 3. 🎨 KBC Visual Design & UI System
- **Custom Hexagonal Geometry**: Custom Jetpack Compose `Shape` (`WgmHexagonShape`) providing beveled/angled corners for question and option cards.
- **Royal Dark Palette**: Deep Royal Purple (`#1A0B36`) and Midnight Blue (`#0D1335`) gradients accented by Neon Cyan option borders (`#00E5FF`).
- **Metallic Gold Accents**: High-grade metallic gold prize badges (`#FACC15` to `#CA8A04`) with dynamic shimmer text animations.
- **Interactive Option States**:
  - `Selected`: Vibrant Yellow/Orange glow with pulsating borders and scale bounce.
  - `Correct`: Electric Green gradient (`#22C55E` to `#15803D`) with celebration aura.
  - `Wrong`: Crimson Red gradient (`#EF4444` to `#991B1B`).
- **Slide-in Money Ladder Drawer**: Dedicated tier ladder displaying current player position (`▶`), safe havens (`🛡️`), and progress markers.
- **Animated Splash Screen**: Custom branded splash screen (`Millionaire.png`) featuring scale, fade-in, and ambient glow effects before game launch.

### 4. 🔊 Audio & Sound System
- **Low-Latency SFX Integration**: Centralized `WgmSoundManager` utilizing `SoundPool` for immediate reaction effects (Lock, Correct, Wrong, Time Up, Question Intro) and `MediaPlayer` for the looping countdown background track.
- **Procedural Tone Engine**: Pure Kotlin `WgmSynthesizer` leveraging `AudioTrack` to generate real-time sine-wave arcade tones for tactile UI interactions.
- **Lifecycle-Aware Audio**: Gracefully pauses, resumes, and frees hardware audio channels adhering to Android activity lifecycles.

---

## 🏛️ Architecture & Tech Stack

The project strictly follows **Clean Architecture** principles and **Unidirectional Data Flow (UDF)**:

```
app/
 ├── src/main/
 │    ├── assets/                    # Audio files (SFX) & Brand Graphics
 │    ├── java/com/wgm/quiz/
 │    │    ├── audio/                # SoundPool, MediaPlayer & AudioTrack synthesizer
 │    │    │    ├── WgmSoundManager.kt
 │    │    │    └── WgmSynthesizer.kt
 │    │    ├── data/                 # Data Layer (Room DB, DAOs, Repositories, Prefs)
 │    │    │    ├── local/           # Room Database & Entities, Score Persistence
 │    │    │    └── repository/      # Repository Implementations
 │    │    ├── domain/               # Domain Layer (Models & Repository Interfaces)
 │    │    │    ├── model/           # WgmQuestion
 │    │    │    └── repository/      # WgmQuizRepository
 │    │    ├── ui/                   # Presentation Layer (100% Jetpack Compose)
 │    │    │    ├── components/      # Hexagon Cards, Timers, Dialogs, Badges
 │    │    │    ├── screens/         # Game Screen & Money Ladder Screen
 │    │    │    └── theme/           # Gradients, Color Tokens, Shapes, Theme
 │    │    ├── viewmodel/            # MVVM StateFlow, UDF GamePhase State Machine
 │    │    ├── MainActivity.kt       # Game Activity
 │    │    ├── SplashActivity.kt     # Animated Splash Entry Point
 │    │    └── WgmApplication.kt     # Application Dependency Container
 │    └── res/                       # Vectors, Mipmaps, Values, Themes
 ├── build.gradle                    # Module Build Configuration
 └── .gitignore                      # Secure and Clean Git Ignore Configuration
```

### Key Technologies
- **Language**: Kotlin 1.9.0
- **UI Framework**: Jetpack Compose + Material 3
- **Architecture**: MVVM + Clean Architecture + StateFlow
- **Database**: Room Persistence Library (`2.6.1`)
- **Persistence**: SharedPreferences / DataStore
- **Audio Engine**: SoundPool + MediaPlayer + AudioTrack
- **Target SDK**: Android 14 (API Level 34)
- **Min SDK**: Android 7.0 (API Level 24)

---

## 🔒 Confidentiality & Repository Cleanliness
- All build outputs (`build/`, `.gradle/`), machine-specific SDK files (`local.properties`), IDE workspace caches (`.idea/`), signing credentials, and keys are strictly filtered via `.gitignore`.
- All redundant boilerplate code, unused starter templates, and empty directories have been pruned for a clean source distribution.
