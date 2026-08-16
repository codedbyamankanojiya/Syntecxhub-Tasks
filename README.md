# 🚀 Syntecxhub Internship Tasks

This repository documents and showcases the weekly production-grade Android development tasks completed during the **Syntecxhub Internship**. Each project is architected with **Modern Android Development (MAD)** best practices, **Clean Architecture**, **Jetpack Compose**, and robust offline-first persistence.

---

## 📁 Tasks Overview

| # | Task & Project | Key Highlights | Core Architecture & Tech |
|---|---|---|---|
| **01** | [**Task 1: SyncTask**](./Task-1) <br> *(Advanced Task Management)* | • WorkManager Reminder Notifications <br>• Dual-direction Swipe Actions & Haptics <br>• Canvas Stats Progress Ring & Analytics <br>• Particle Celebration Burst Effects | **Clean Architecture + MVVM** <br>Jetpack Compose, Dagger Hilt, Room DB, WorkManager, Coroutines & Flow |
| **02** | [**Task 2: WGM Quiz**](./Task-2) <br> *(Who's Gonna Be Millionaire)* | • 16-Tier Dynamic Money Ladder (₹1K – ₹7Cr) <br>• Complete 4-Lifeline Engine (50:50, Poll, Flip, Life) <br>• SoundPool / MediaPlayer Audio & Synth Engine <br>• Custom Hexagonal Geometry & Glowing UI <br>• Animated Splash Screen with Logo Reveal | **Clean Architecture + MVVM (UDF)** <br>100% Jetpack Compose, Room DB, StateFlow, SoundPool / AudioTrack, SharedPreferences |

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
  - **Immersive Home Screen:** Full-screen dark navy/purple background with animated spotlight rays, pulsing gold logo halo, tagline card, and a breathing-pulse gold START QUIZ button — no clutter, just the show's signature energy.
  - **45-Question Tiered Bank:** Fully rewritten question engine with 3 real, hand-crafted questions per level across 15 difficulty tiers — from easy pop culture to expert-level quantum physics and Indian history.
  - **Dynamic Prize Ladder:** Progressive 16-level money ladder (₹1,000 → ₹7 Crores) with Safe-Haven checkpoints at levels 5 and 10.
  - **4-Lifeline System:** 50:50, Difficulty-weighted Audience Poll, Flip the Question, and Extra Life pop-up revival.
  - **Dual MediaPlayer Audio Engine:** `Question.mp3` loops as an atmospheric background when each question loads; `Timer.mp3` takes over for the 30s countdown; 5 SoundPool SFX handle Lock, Correct, Wrong, Time Up, and Click events.
  - **Data & Economy:** Room DB v2 (auto-migrates and re-seeds) with persistent score/coin economy via SharedPreferences.
- **Tech Stack:** Kotlin, Jetpack Compose, Material 3, Room DB v2, Kotlin Coroutines, StateFlow, Dual MediaPlayer + SoundPool, SharedPreferences.
- **Documentation:** Explore the full [Task-2 README](./Task-2/README.md).

---

## 🛠️ Global Engineering Standards

Across all tasks in this repository:
- **Clean Architecture & Separation of Concerns:** Unidirectional Data Flow (UDF) with independent Data, Domain, and Presentation layers.
- **100% Declarative UI:** Zero legacy XML layouts — built entirely with Jetpack Compose & Material 3 design tokens.
- **Robust Persistence:** Offline-first architecture powered by Room Database and structured local storage.
- **Security & Cleanliness:** Production-ready `.gitignore` configurations protecting local environments, credentials, and build caches.

---

Built with ❤️ by **Aman Kanojiya** for the **Syntecxhub Internship and Virtual Training Program**.
