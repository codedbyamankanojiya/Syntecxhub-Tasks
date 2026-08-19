# WGM Quiz — Who's Gonna Be Millionaire 🏆

A modern, high-production Android quiz application inspired by the iconic *Kaun Banega Crorepati / Who Wants to Be a Millionaire* game show format. Built with **100% Jetpack Compose**, **Kotlin Coroutines**, **Room Database**, **SoundPool & Triple MediaPlayer Audio Engine**, and **Clean Architecture (MVVM + UDF)**.

---

## 🌟 Highlights & Features

### 1. 🎨 Immersive Home Screen & Iconic BGM
- **Iconic Ambient Background Music**: Features `WGM Home.mp3` playing smoothly in a loop upon launching the home screen to build game-show suspense.
- **Full-Screen Immersive Visuals**: Deep dark navy-to-midnight-blue vertical gradient background with animated radial spotlight rays.
- **Animated Millionaire Logo**: The iconic circular "Who's Gonna Be Millionaire" badge displayed at 260dp with a pulsing gold halo (animated `glowAlpha`).
- **Tagline Card**: Semi-transparent dark purple card displaying *"One Question. / One Step Closer to a Million."*
- **Gold START QUIZ Button**: Horizontal gold gradient pill (`#E6A800 → #FFC107`) with a circular ▶ play icon and subtle breathing pulse animation.

### 2. 🎮 Core Game Engine & Mechanics
- **Dynamic 15-Tier Prize Progression**: Progressive 15-level money ladder from **₹1,000** up to the grand jackpot of **₹7 Crores**, paired with tiered coin earnings (100 to 2,000 coins).
- **Safe-Haven Milestones**: Checkpoint thresholds securing earned cash even on a wrong answer:
  - **Tier 1**: Level 5 (₹10,000 / 500 Coins)
  - **Tier 2**: Level 10 (₹3,20,000 / 1,000 Coins)
  - **Jackpot**: Level 15 (₹7,00,00,000 / 2,000 Coins)
- **30-Second Active Countdown**: Circular countdown timer with real-time arc animation and urgency color transitions (Gold → Orange → Red).
- **Persistent Economy & Scoring**: Tracks coins earned, high scores, games played, and best levels saved locally across sessions via SharedPreferences / DataStore.

### 3. 🪜 Redesigned Money Ladder Screen
- **Top Jackpot Showcase**: Prominent gold hexagonal banner showcasing the ultimate ₹7 Crores prize and 2,000 coin bounty.
- **Current Level Indicator**: Highlights active standing with a custom Player Avatar badge (`AvatarWithBadge`) and vibrant green hexagonal card.
- **Progress Tracking**: Clear distinction between completed levels, active level, and upcoming milestones with dimming and glowing state cues.
- **Quick Overlay Access**: Tap the `₹` icon on the game top bar anytime to inspect the ladder; tap anywhere to dismiss and resume gameplay.

### 4. 🛟 Complete 4-Lifeline System & Custom Favicons
- **50:50 (`50:50`)**: Instantly eliminates two incorrect choices with smooth visual fade out.
- **Audience Poll (`👥`)**: Displays difficulty-weighted percentage distributions in a custom dialogue featuring a 3-person silhouette icon.
- **Flip the Question (`🔄`)**: Swaps the current question with a fresh alternative of equal difficulty using a smooth refresh interaction.
- **Extra Life (`❤️`)**: Automatically triggers a revival modal on an incorrect answer, allowing the player to stay in the game or walk away with guaranteed earnings.

### 5. 📝 Question Bank — 45 Hand-Crafted Questions, 15 Difficulty Tiers
The question engine is powered by **45 real-world questions** (3 per difficulty tier), ensuring varied replayability:

| Levels | Category | Examples |
|--------|----------|---------|
| 1–3 | Easy: Pop culture, everyday life, India basics | Leap year days, Twitter characters, India's capital |
| 4–6 | Medium-Easy: Sports, geography, history | FIFA World Cup, Nile River, India's independence year |
| 7–9 | Medium: Science, tech, Indian culture | Android creator, speed of light, 'Jana Gana Mana' author |
| 10–12 | Hard: World history, advanced science, mathematics | Treaty of Versailles, Euler's number, atomic number of Gold |
| 13–15 | Expert: Deep Indian history, computer science, quantum physics | Battle of Plassey, O(n log n), Riemann Hypothesis |

### 6. 🔊 Audio System — Triple MediaPlayer + SoundPool Architecture
All 7 audio tracks and sound effects are engineered for low latency and dynamic transitions:

| Asset | Engine | Trigger |
|-------|--------|---------|
| `WGM Home.mp3` | **MediaPlayer (looping)** | Plays atmospheric theme on Home screen; pauses on quiz start |
| `Question.mp3` | **MediaPlayer (looping)** | Plays tension background when each question loads |
| `Timer.mp3` | **MediaPlayer (looping)** | Replaces Question background when 30s countdown ticks |
| `Lock.mp3` | **SoundPool (one-shot)** | On answer selection / lock |
| `Right Answer.mp3` | **SoundPool (one-shot)** | Correct answer reveal & celebration |
| `Wrong Answer.mp3` | **SoundPool (one-shot)** | Incorrect answer reveal |
| `Time Up.mp3` | **SoundPool (one-shot)** | Timer reaches 0 seconds |

**Audio Flow & Lifecycle Management**:
`Home BGM` → `Quiz Start` → `Question BGM` → `Timer BGM` → `Answer Locked (SFX)` → `Result (SFX)`.
All audio players are fully lifecycle-aware, pausing and resuming cleanly across Activity lifecycle events (`onPause`, `onResume`, `onDestroy`).

---

## 🏛️ Architecture & Tech Stack

Strictly follows **Clean Architecture** with **Unidirectional Data Flow (UDF)**:

```
app/
 ├── src/main/
 │    ├── assets/                    # Audio tracks (7 MP3s) & Millionaire.png logo
 │    ├── java/com/wgm/quiz/
 │    │    ├── audio/
 │    │    │    ├── WgmSoundManager.kt   # Triple MediaPlayer + SoundPool engine
 │    │    │    └── WgmSynthesizer.kt    # Pure Kotlin AudioTrack sine-wave generator
 │    │    ├── data/
 │    │    │    ├── local/               # Room DB (v2), DAO, Entities, ScoreRepository
 │    │    │    └── repository/          # RepositoryImpl — 45-question seed data
 │    │    ├── domain/
 │    │    │    ├── model/               # WgmQuestion domain model
 │    │    │    └── repository/          # WgmQuizRepository interface
 │    │    ├── ui/
 │    │    │    ├── components/          # Hexagon cards, Timer, Dialogs, Badges, Lifelines
 │    │    │    ├── screens/             # WgmHomeScreen, WgmGameScreen, WgmMoneyLadderScreen
 │    │    │    └── theme/               # Color tokens, gradients, shapes, typography
 │    │    ├── viewmodel/                # MVVM StateFlow UDF game state machine (GamePhase)
 │    │    ├── MainActivity.kt
 │    │    ├── SplashActivity.kt
 │    │    └── WgmApplication.kt
 │    └── res/
 ├── build.gradle
 └── .gitignore
```

### Key Technologies
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose + Material 3 (Material Icons Extended)
- **Architecture**: MVVM + Clean Architecture + UDF (`StateFlow`, `GamePhase`)
- **Database**: Room v2 (with automatic migration & re-seeding)
- **Persistence**: SharedPreferences / DataStore (score, high score & coin economy)
- **Audio Engine**: Triple MediaPlayer (`WGM Home`, `Question`, `Timer`) + SoundPool (4 short SFX)
- **Target SDK**: Android 15 / 14 (API Level 37 / Compile SDK 37)
- **Min SDK**: Android 7.0 (API Level 24)

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug / Koala / Hedgehog (2023.1.1+)
- **JDK**: Java Development Kit 17
- **Android SDK**: API Level 34+ (Compile SDK 37)

### Building & Running
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/codedbyamankanojiya/Syntecxhub-Tasks.git
   cd Syntecxhub-Tasks/Task-2
   ```
2. **Open in Android Studio** and navigate to the `Task-2` directory.
3. **Configure JDK 17** via `Settings → Build, Execution, Deployment → Build Tools → Gradle`.
4. **Build & Run** on your target device or emulator (**Shift + F10**).

> **Note**: Room DB is configured at version 2 with `fallbackToDestructiveMigration()`. On first launch, the database automatically seeds all 45 hand-crafted questions across all 15 difficulty tiers.

---

## 🔒 Confidentiality & Repository Cleanliness
- All build outputs (`build/`, `.gradle/`), machine-specific SDK files (`local.properties`), IDE workspace caches (`.idea/`), signing credentials, and keys are filtered via `.gitignore`.

Built with ❤️ by **Aman Kanojiya** for **Syntecxhub Internship & Virtual Training Program**.