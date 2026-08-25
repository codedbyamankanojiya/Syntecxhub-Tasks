# 📺 DeshNews 24/7 — Production-Ready Broadcast TV News App

An authentic, production-grade Android news application inspired by high-contrast Indian TV Studio broadcast aesthetics. Engineered with **100% Jetpack Compose (Material 3)**, **Clean Architecture + MVVM (Unidirectional Data Flow)**, **Jsoup Full-Text Extraction**, **Room Database Offline-First Caching**, and **Dagger Hilt Dependency Injection**.

---

## 📑 Table of Contents
1. [Overview & Visual Aesthetic](#-overview--visual-aesthetic)
2. [Key Visual Directives & Design System](#-key-visual-directives--design-system)
3. [Advanced Features](#-advanced-features)
4. [Architecture & Engineering Blueprint](#-architecture--engineering-blueprint)
5. [Data Layer & Offline-First Strategy](#-data-layer--offline-first-strategy)
6. [Core Screens & Components](#-core-screens--components)
7. [API Integration (GNews REST API)](#-api-integration-gnews-rest-api)
8. [Directory & File Structure](#-directory--file-structure)
9. [Setup, Build & Execution Guide](#-setup-build--execution-guide)

---

## 🌟 Overview & Visual Aesthetic

**DeshNews 24/7** delivers a live studio broadcast atmosphere to mobile news browsing. The app utilizes a high-contrast dark palette, urgent crimson breaking banners, gold highlights, edge-to-edge photography, and instant offline accessibility.

Powered by the **GNews REST API**, the application streams breaking top headlines filtered for India (`country=in`), stores them locally in **Room Database**, and uses **Jsoup-based background scraping** to provide a native, full-text reading experience without external browser redirects.

---

## 🎨 Key Visual Directives & Design System

### 1. Curated Color Palette
| Token | Hex Value | Application |
|---|---|---|
| **Deep Navy (Canvas)** | `#0A0E17` | Root window background & status/navigation bar fill |
| **Dark Slate (Ambient)** | `#111827` | Subtle vertical gradient shading |
| **Card Surface** | `#151C2C` | Headline cards, bottom navigation bar, chip containers |
| **Card Border** | `#1F293D` | 1dp crisp dividers & card stroke borders |
| **Broadcast Red** | `#DC2626` | Header accents, active tab indicators, and Offline banners |
| **Studio Gold** | `#FACC15` | `DeshNews` brand segment, pager indicator dots, bookmark stars |
| **Live Green** | `#22C55E` | 🟢 `LIVE` studio status chips |

### 2. Iconic Broadcast UI Elements
- **Dual-Tone Brand Logo (`BreakingNewsBadge`):**
  Split horizontal pill featuring `DeshNews` in black bold text on Studio Gold (`#FACC15`) fused directly with `24/7` in white bold text on Broadcast Red (`#DC2626`).
- **3D Studio Carousel (`StudioBannerCarousel`):**
  A horizontal pager presenting featured stories with animated width-morphing gold indicators.
- **Collapsible Search Interface:**
  A professional, space-saving search icon that expands into a full-width search bar with real-time query handling and keyboard actions.

---

## 🚀 Advanced Features

### 1. Native Full-Text Extraction (Jsoup)
The app goes beyond snippets. Using **Jsoup background scraping**, DeshNews 24/7 fetches the source webpage, extracts the core article text, and renders it natively within the app UI. This eliminates the need for external browsers and provides a theme-consistent reading experience.

### 2. Smart Offline Experience
When the network is lost, the app doesn't crash or show a blank screen. Instead:
- An **`OfflineBanner`** appears with a "Tap to Retry" action.
- The app serves the last articles from the **Room Cache**.
- Users can still browse, read summaries, and view bookmarked news offline.

### 3. Professional Search & Empty States
The search interface is not just functional but resilient. If a search query yields no results, a dedicated **Search Empty State** appears with a custom illustration and a quick "Clear Search" action to restore the main feed.

### 4. Professional Settings Suite
A centralized **Modal Bottom Sheet** allows users to:
- Toggle between **Dark Mode** and **Light Mode** instantly.
- View application version and build information.
- Access system-wide controls in a clean, uncluttered interface.

---

## 🏛️ Architecture & Engineering Blueprint

The codebase strictly follows **Clean Architecture** and **Unidirectional Data Flow (UDF)**:

```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                      │
│      NewsHomeScreen      •      NewsDetailScreen            │
│      FullNewsScreen (Reader) • SettingsBottomSheet          │
│                              ▲                              │
│                              │ StateFlow<NewsUiState>       │
│                              │ Intents (search, refresh)    │
│                              ▼                              │
│                        NewsViewModel                        │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                        DOMAIN LAYER                         │
│      NewsArticle (Model)  •  fetchFullArticleContent (Jsoup)│
│      NewsRepository (Contract Interface)                    │
└──────────────────────────────▲──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                         DATA LAYER                          │
│      NewsRepositoryImpl  (Offline-First Cache Coordinator)  │
│      ├── NewsApiService (Retrofit + Kotlinx Serialization)  │
│      ├── Jsoup Content Extractor (Web Scraping)             │
│      └── NewsDao / NewsDatabase (Room Storage)              │
└─────────────────────────────────────────────────────────────┘
```

---

## 💾 Data Layer & Offline-First Strategy

1. **Immediate Cache Emission:** Backed directly by Room queries (`ORDER BY cachedAt DESC`).
2. **Background Synchronization:** Fetches JSON payloads from GNews and merges them into the local SQLite store.
3. **Pull-to-Refresh:** Standard Material 3 integration allowing users to trigger fresh fetches with a simple swipe-down gesture.
4. **Search Persistence:** Search queries are handled efficiently via the GNews search endpoint, with results displayed in a native list view.

---

## 🌐 API Integration (GNews REST API)

Configured with the GNews REST endpoint (`https://gnews.io/api/`). The API key is injected at compile-time via `BuildConfig.GNEWS_API_KEY`, secured through `local.properties`.

---

## 📁 Directory & File Structure

```
Task-3/
├── app/
│   └── src/main/java/com/deshnews/app/
│       ├── MainActivity.kt                   # NavHost & Reader-Mode entry
│       ├── data/
│       │   ├── local/                        # Room Database & Entities
│       │   ├── remote/                       # Retrofit & DTOs
│       │   └── repository/                   # Offline-first & Jsoup logic
│       ├── domain/                           # Business logic & Model
│       └── presentation/
│           ├── viewmodel/                    # State management
│           └── ui/
│               ├── components/               # Brand logo, Cards, Carousel
│               ├── screen/                   # Home, Detail, Full Story
│               └── theme/                    # Color, Typography, Theme
├── gradle/libs.versions.toml                 # Version Catalog (Jsoup, Browser, etc.)
└── README.md                                 # Documentation
```

---

## 🚀 Setup, Build & Execution Guide

### Prerequisites
- **Android Studio:** Ladybug / Iguana or later.
- **JDK:** Java 17.
- **Target / Compile SDK:** 34.
- **Minimum OS:** Android 8.0 (API Level 26).

### Quick Start
1. **Open Project:** Open `Task-3` in Android Studio.
2. **API Key:** Add your GNews API Key to `local.properties`:
   ```properties
   GNEWS_API_KEY=YOUR_ACTUAL_API_KEY_HERE
   ```
3. **Build & Run:** Select a device and press **`Shift + F10`**.

---

## 👨‍💻 Author & Credits

- **Developer:** Aman Kanojiya
- **Program:** Syntecxhub Internship & Virtual Training Program
- **Repository:** [Syntecxhub-Tasks](https://github.com/codedbyamankanojiya/Syntecxhub-Tasks)

*Built with ❤️ for Modern Android Development.*
