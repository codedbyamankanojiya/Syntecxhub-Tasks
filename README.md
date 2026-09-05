# 🚀 Syntecxhub Internship Tasks

This repository documents and showcases the weekly production-grade Android development tasks completed during the **Syntecxhub Internship**. Each project is architected with **Modern Android Development (MAD)** best practices, **Clean Architecture**, **Jetpack Compose**, and robust offline-first persistence.

---

## 📁 Tasks Overview

| # | Task & Project | Key Highlights | Core Architecture & Tech |
|---|---|---|---|
| **01** | [**Task 1: SyncTask**](./Task-1) <br> *(Advanced Task Management)* | • WorkManager Reminder Notifications <br>• Dual-direction Swipe Actions & Haptics <br>• Canvas Stats Progress Ring & Analytics <br>• Particle Celebration Burst Effects | **Clean Architecture + MVVM** <br>Jetpack Compose, Dagger Hilt, Room DB, WorkManager, Coroutines & Flow |
| **02** | [**Task 2: WGM Quiz**](./Task-2) <br> *(Who's Gonna Be Millionaire)* | • 15-Tier Dynamic Money Ladder (₹1K – ₹7Cr) <br>• Complete 4-Lifeline Engine (50:50, Poll, Flip, Life) <br>• Triple MediaPlayer + SoundPool Audio Architecture <br>• Custom Hexagonal Geometry & Glowing UI <br>• Animated Splash Screen & Iconic Home Theme | **Clean Architecture + MVVM (UDF)** <br>100% Jetpack Compose, Room DB v2, StateFlow, SoundPool + Triple MediaPlayer, DataStore |
| **03** | [**Task 3: DeshNews 24/7**](./Task-3) <br> *(Broadcast TV News App)* | • High-Contrast Indian TV Studio Aesthetic (#0A0E17, #DC2626, #FACC15) <br>• Dual-Tone DeshNews 24/7 Brand Badge & 3D Stage Carousel <br>• Native Jsoup Full-Text Web Extraction & In-App Reader <br>• Offline-First Room DB Caching with Offline Banner & Retry <br>• Collapsible Search with Custom Empty State & Settings Modal Sheet | **Clean Architecture + MVVM (UDF)** <br>100% Jetpack Compose M3, Dagger Hilt, Jsoup, Retrofit 2, Kotlinx Serialization, Room DB, Coil, StateFlow |
| **04** | [**Task 4: NovaChat**](./Task-4) <br> *(Real-Time Cloud Messaging)* | • Pure Email/Password Auth & Reset Flow <br>• Sub-second Firestore Chat + Room Offline Sync <br>• 3-Stage Read Receipts (Sent, Delivered, Blue Read) <br>• 12-Avatar Grid System & Live Name Sync <br>• Unread Blips, Navigation Badges & Push Notifications <br>• Account Deletion & Disabled State Protection | **Clean Architecture + MVVM (UDF)** <br>100% Jetpack Compose M3, Dagger Hilt, Cloud Firestore, Firebase Auth, Room SQLite, Coil, StateFlow |

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
An authentic, production-grade Android news application inspired by high-contrast Indian TV Studio broadcast aesthetics, engineered with full offline-first caching, Jsoup web scraping, and unidirectional data flow.
- **Key Features:**
  - **Broadcast Studio Visual Design:** Curated palette featuring Deep Navy canvas (`#0A0E17`), Dark Slate ambient gradient (`#111827`), Card surface (`#151C2C`), crisp borders (`#1F293D`), Broadcast Red accents (`#DC2626`), Studio Gold highlights (`#FACC15`), and Live Green indicators (`#22C55E`).
  - **Iconic Dual-Tone Badge & 3D Stage Carousel:** Split horizontal pill brand logo (`DeshNews` Gold + `24/7` Red) and horizontal banner carousel featuring animated morphing gold indicators.
  - **Native Full-Text Extraction (Jsoup):** In-app web scraping engine extracting full article contents directly from news sources, providing a distraction-free, theme-consistent reading view without external browser redirects.
  - **Smart Offline-First Architecture:** Instant cache emission backed by Room Database, silent background network sync, pull-to-refresh gestures, persistent article bookmarks, and an interactive `OfflineBanner` with tap-to-retry actions.
  - **Collapsible Search & Custom Empty States:** Real-time news search with a responsive collapsible search bar and a dedicated illustration-backed empty state with quick search reset.
  - **Centralized Settings Suite:** Professional Modal Bottom Sheet providing instant Dark/Light mode theme switching and application version/build metadata.
  - **Secure API Configuration:** GNews REST API integrated via Retrofit 2 + Kotlinx Serialization, with compile-time API key injection through git-ignored `local.properties`.
- **Tech Stack:** Kotlin, Jetpack Compose Material 3, Dagger Hilt, Jsoup, Retrofit 2, OkHttp, Kotlinx Serialization, Room Database, Coil, StateFlow.
- **Documentation:** Explore the full [Task-3 README](./Task-3/README.md).

---

### 🔹 [Task 4: NovaChat — Production Real-Time Chat Application 💬](./Task-4)
A production-grade real-time chat application for Android crafted with a Warm Minimal aesthetic, powered by Firebase Cloud Firestore, Firebase Authentication, and an offline-first Room SQLite database.
- **Key Features:**
  - **Warm Minimal Design Aesthetic:** Curated palette featuring warm off-white canvas (`#FAF9F6`), pure white surfaces (`#FFFFFF`), Deep Teal branding (`#1A6B5C`), Lighter Teal outgoing bubbles (`#1A8D7F`), Cyan-Blue read indicators (`#0288D1`), and Coral accent badges (`#E85D4A`).
  - **Pure Email/Password Authentication & Password Reset:** Secure email/password login and registration with live input validation, dedicated Forgot Password reset email dispatch, and in-app Change Password & Change Email with secure credential re-authentication.
  - **Real-Time 1-on-1 Messaging & Offline Sync:** Sub-second instant messaging powered by Cloud Firestore snapshot listeners paired with background Room SQLite database caching for instant cold starts.
  - **3-Stage WhatsApp-Style Read Receipts:** Comprehensive real-time delivery lifecycle — Single white tick (Sent to server), Double white ticks (Delivered to recipient's device), and Double cyan-blue ticks (Read/seen by recipient).
  - **Intelligent Unread Alerting & Notifications:** Unread message blip dots on conversation avatars, bold unread preview typography, coral badge pill count, bottom navigation bar unread counter, and Android push/local notifications with high-importance heads-up banners and direct room routing.
  - **Avatar System & Real-Time Identity Sync:** 12-avatar randomized avatar generator with rich color gradients, instant Firestore synchronization, and global display name updates across all chat headers and conversation records.
  - **Account Deletion & Disabled Partner Handling:** Complete account purge (deleting Firestore user profile and Firebase Auth account) with immediate logout back to sign-in; conversation partners automatically see "Deleted User" with disabled message input.
  - **Live Presence & Typing Engine:** Online presence automatically synchronized with the Android Activity lifecycle and debounced 2-second typing indicators.
  - **Cluster-Aware Message Bubbles:** Modern rounded bubble shapes that smoothly group consecutive messages from the same sender.
  - **Hardened Zero-Leak Security & Deployment-Ready:** Multi-level git-ignored credential protection for `google-services.json`, keystores, and environment variables with ProGuard/R8 optimization rules and production-ready Gradle release builds.
- **Tech Stack:** Kotlin, Jetpack Compose Material 3, Dagger Hilt, Cloud Firestore, Firebase Auth, Room SQLite Database, Coil, StateFlow.
- **Documentation:** Explore the full [Task-4 README](./Task-4/README.md).

---

## 🛠️ Global Engineering Standards

Across all tasks in this repository:
- **Clean Architecture & Separation of Concerns:** Unidirectional Data Flow (UDF) with independent Data, Domain, and Presentation layers.
- **100% Declarative UI:** Zero legacy XML layouts — built entirely with Jetpack Compose & Material 3 design tokens.
- **Robust Persistence & Cloud Integration:** Offline-first architecture powered by Room SQLite Database, Firebase Cloud Firestore, and structured local storage.
- **Security & Cleanliness:** Production-ready `.gitignore` configurations protecting local environments, credentials, API keys, and build caches across all project submodules.

---

Built with ❤️ by **Aman Kanojiya** for the **Syntecxhub Internship and Virtual Training Program**.
