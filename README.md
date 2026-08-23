# 🚀 Syntecxhub Internship Tasks

This repository documents and showcases the weekly production-grade Android development tasks completed during the **Syntecxhub Internship**. Each project is architected with **Modern Android Development (MAD)** best practices, **Clean Architecture**, **Jetpack Compose**, and robust offline-first persistence.

---

## 📁 Tasks Overview

| # | Task & Project | Key Highlights | Core Architecture & Tech |
|---|---|---|---|
| **01** | [**Task 1: SyncTask**](./Task-1) <br> *(Advanced Task Management)* | • WorkManager Reminder Notifications <br>• Dual-direction Swipe Actions & Haptics <br>• Canvas Stats Progress Ring & Analytics <br>• Particle Celebration Burst Effects | **Clean Architecture + MVVM** <br>Jetpack Compose, Dagger Hilt, Room DB, WorkManager, Coroutines & Flow |
| **02** | [**Task 2: WGM Quiz**](./Task-2) <br> *(Who's Gonna Be Millionaire)* | • 15-Tier Dynamic Money Ladder (₹1K – ₹7Cr) <br>• Complete 4-Lifeline Engine (50:50, Poll, Flip, Life) <br>• Triple MediaPlayer + SoundPool Audio Architecture <br>• Custom Hexagonal Geometry & Glowing UI <br>• Animated Splash Screen & Iconic Home Theme | **Clean Architecture + MVVM (UDF)** <br>100% Jetpack Compose, Room DB v2, StateFlow, SoundPool + Triple MediaPlayer, DataStore |
| **03** | [**Task 3: DeshNews 24/7**](./Task-3) <br> *(Broadcast TV News App)* | • High-Contrast Dark Broadcast Studio UI (#0A0E17, #DC2626, #FACC15) <br>• Dual-Tone BREAKING\|NEWS Badge & 3D Stage Carousel <br>• GNews REST API Integration & Retrofit 2 + Kotlinx Serialization <br>• Room Database Offline-First Caching & Bookmark Persistence <br>• Edge-to-Edge Article Detail View with Timestamp Chips & Related Rail | **Clean Architecture + MVVM (UDF)** <br>100% Jetpack Compose M3, Dagger Hilt, Room DB, Retrofit 2, OkHttp, Coil, StateFlow |

---

### 🔹 [Task 1: SyncTask — Senior-Level Task & Productivity Management](./Task-1)
A production-grade task orchestration application emphasizing polished micro-interactions and strict layered engineering.
- **Key Features:** Priority matrix, WorkManager date/time scheduled notifications, animated sweep-gradient progress ring, search & status chip filters, and particle confetti effects.
- **Tech Stack:** Kotlin, Jetpack Compose, Material 3, Dagger Hilt, Room, WorkManager, Coroutines & Flow.
- **Documentation:** Explore the full [Task-1 README](./Task-1/README.md).

---

### 🔹 [Task 2: WGM Quiz — Who's Gonna Be Millionaire 🏆](./Task-2)
An interactive, high-stakes quiz game built around the classic *Kaun Banega Crorepati / Who Wants to Be a Millionaire* format.
- **Key Features:**
  - **Immersive Home Screen with Iconic BGM:** Full-screen dark navy/purple gradient background with animated spotlight rays, pulsing gold logo halo, tagline card, breathing-pulse gold START QUIZ button, and atmospheric `WGM Home.mp3` theme.
  - **45-Question Tiered Bank:** 3 hand-crafted, real-world questions per level across 15 difficulty tiers — from everyday pop culture and sports to expert-level quantum physics and Indian history.
  - **Redesigned Money Ladder Screen:** Dedicated 15-tier prize progression (₹1,000 → ₹7 Crores / 100 → 2,000 Coins) with active player avatar position marker and Safe-Haven checkpoints at levels 5, 10, and 15.
  - **4-Lifeline System & Custom Favicons:** 50:50 text pill, 3-person silhouette Audience Poll, Flip the Question refresh, and Extra Life pop-up revival dialog.
  - **Triple MediaPlayer + SoundPool Audio Engine:** Looping MediaPlayers for `WGM Home.mp3`, `Question.mp3`, and `Timer.mp3` countdown; crisp SoundPool SFX for Lock, Correct, Wrong, and Time Up events.
  - **Data & Economy:** Room DB v2 (with automatic migration & seeding) with persistent score, coin economy, and high scores.
- **Tech Stack:** Kotlin, Jetpack Compose, Material 3, Room DB v2, Kotlin Coroutines, StateFlow, Triple MediaPlayer + SoundPool, DataStore / SharedPreferences.
- **Documentation:** Explore the full [Task-2 README](./Task-2/README.md).

---

### 🔹 [Task 3: DeshNews 24/7 — Broadcast TV News App 📺](./Task-3)
A production-ready news application modeled after a classic Indian TV Studio broadcast aesthetic with high-contrast visuals, live breaking banners, and full offline caching.
- **Key Features:**
  - **Broadcast Visual Design:** Deep Navy canvas (`#0A0E17`), Electric Crimson headers (`#DC2626`), and Studio Gold accents (`#FACC15`).
  - **Iconic Dual-Tone Badge:** Split-box `BREAKING` (Gold/Black) + `NEWS` (Red/White) badge with live notification counter pill.
  - **3D Studio Carousel:** Horizontal pager presenting top stories as dual-pane 3D stage cards with animated gold indicators.
  - **Compact Headlines & Edge-to-Edge Detail:** Sleek card stack with red chips and full-bleed article reading experience with timestamp chips (`📅 Date | 🕐 Time | 🟢 LIVE`).
  - **Offline-First Room Caching:** Instant cache retrieval on launch, silent background network synchronization, and persistent article bookmarks.
- **Tech Stack:** Kotlin, Jetpack Compose Material 3, Dagger Hilt, Retrofit 2, OkHttp, Kotlinx Serialization, Room Database, Coil, StateFlow.
- **Documentation:** Explore the full [Task-3 README](./Task-3/README.md).

---

## 🛠️ Global Engineering Standards

Across all tasks in this repository:
- **Clean Architecture & Separation of Concerns:** Unidirectional Data Flow (UDF) with independent Data, Domain, and Presentation layers.
- **100% Declarative UI:** Zero legacy XML layouts — built entirely with Jetpack Compose & Material 3 design tokens.
- **Robust Persistence:** Offline-first architecture powered by Room Database and structured local storage.
- **Security & Cleanliness:** Production-ready `.gitignore` configurations protecting local environments, credentials, and build caches.

---

Built with ❤️ by **Aman Kanojiya** for the **Syntecxhub Internship and Virtual Training Program**.
