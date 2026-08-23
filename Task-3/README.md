# 📺 DeshNews 24/7 — Production-Ready Broadcast TV News App

An authentic, production-grade Android news application inspired by high-contrast Indian TV Studio broadcast aesthetics. Engineered with **100% Jetpack Compose (Material 3)**, **Clean Architecture + MVVM (Unidirectional Data Flow)**, **Retrofit 2 + Kotlinx Serialization**, **Room Database Offline-First Caching**, and **Dagger Hilt Dependency Injection**.

---

## 📑 Table of Contents
1. [Overview & Visual Aesthetic](#-overview--visual-aesthetic)
2. [Key Visual Directives & Design System](#-key-visual-directives--design-system)
3. [Architecture & Engineering Blueprint](#-architecture--engineering-blueprint)
4. [Data Layer & Offline-First Strategy](#-data-layer--offline-first-strategy)
5. [Core Screens & Components](#-core-screens--components)
6. [API Integration (GNews REST API)](#-api-integration-gnews-rest-api)
7. [Directory & File Structure](#-directory--file-structure)
8. [Setup, Build & Execution Guide](#-setup-build--execution-guide)

---

## 🌟 Overview & Visual Aesthetic

**DeshNews 24/7** delivers a live studio broadcast atmosphere to mobile news browsing. The app utilizes a high-contrast dark palette, urgent crimson breaking banners, gold highlights, edge-to-edge photography, and instant offline accessibility.

Powered by the **GNews REST API**, the application streams breaking top headlines filtered for India (`country=in`), stores them locally in **Room Database**, and ensures bookmark persistence across cache refreshes.

---

## 🎨 Key Visual Directives & Design System

### 1. Curated Color Palette
| Token | Hex Value | Application |
|---|---|---|
| **Deep Navy (Canvas)** | `#0A0E17` | Root window background & status/navigation bar fill |
| **Dark Slate (Ambient)** | `#111827` | Subtle vertical gradient shading |
| **Card Surface** | `#151C2C` | Headline cards, bottom navigation bar, chip containers |
| **Card Border** | `#1F293D` | 1dp crisp dividers & card stroke borders |
| **Broadcast Red** | `#DC2626` | Breaking banners, header accents, active tab indicators |
| **Studio Gold** | `#FACC15` | `BREAKING` badge pill, pager indicator dots, bookmark stars |
| **Live Green** | `#22C55E` | 🟢 `LIVE` studio status chips |
| **Notification Red** | `#EF4444` | Top bar notification counter badge |

### 2. Iconic Broadcast UI Elements
- **Dual-Tone Breaking Badge (`BreakingNewsBadge`):**
  Split horizontal pill featuring `BREAKING` in black bold text on Studio Gold (`#FACC15`) fused directly with `NEWS` in white bold text on Broadcast Red (`#DC2626`).
- **3D Studio Carousel (`StudioBannerCarousel`):**
  A horizontal pager presenting featured stories in a dual-pane stage card:
  - *Left Pane:* Urgent gradient red banner (`#DC2626 → #991B1B`) with category tags and high-contrast title.
  - *Right Pane:* Sharp high-resolution news photograph with horizontal gradient seam blending.
  - *Pager Dots:* Gold indicators with animated width morphing (`6dp ↔ 18dp`) on page transitions.
- **Compact Headline Cards (`HeadlineCard`):**
  Horizontal cards featuring a red `BREAKING` micro-chip, 2-line title typography, source attribution, and right-aligned 88×70dp thumbnail.
- **Edge-to-Edge Article Detail (`NewsDetailScreen`):**
  Full-bleed 300dp cover image with translucent gradient overlay, back/bookmark/share action toolbar, timestamp chips (`📅 Date | 🕐 Time | 🟢 LIVE`), structured paragraphs, and a horizontal "Related News" rail.

---

## 🏛️ Architecture & Engineering Blueprint

The codebase strictly follows **Clean Architecture** and **Unidirectional Data Flow (UDF)**:

```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                      │
│      NewsHomeScreen      •      NewsDetailScreen            │
│                              ▲                              │
│                              │ StateFlow<NewsUiState>       │
│                              │ Intents (refresh, bookmark)  │
│                              ▼                              │
│                        NewsViewModel                        │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                        DOMAIN LAYER                         │
│      NewsArticle (Model)  •  GetHeadlinesUseCase            │
│      NewsRepository (Contract Interface)                    │
└──────────────────────────────▲──────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────┐
│                         DATA LAYER                          │
│      NewsRepositoryImpl  (Offline-First Cache Coordinator)  │
│      ├── NewsApiService (Retrofit + Kotlinx Serialization)  │
│      └── NewsDao / NewsDatabase (Room Entity & Storage)     │
└─────────────────────────────────────────────────────────────┘
```

### Dependency Injection (Dagger Hilt)
- **`NetworkModule.kt`**: Provides configured `OkHttpClient` (30s timeouts, logging interceptor) and `Retrofit` with `asConverterFactory("application/json")`.
- **`DatabaseModule.kt`**: Builds the `NewsDatabase` singleton and exposes `NewsDao`.
- **`RepositoryModule.kt`**: Uses `@Binds` to bind `NewsRepositoryImpl` to `NewsRepository`.

---

## 💾 Data Layer & Offline-First Strategy

1. **Immediate Cache Emission:** `repository.getHeadlines()` returns a cold `Flow<List<NewsArticle>>` backed directly by Room queries (`SELECT * FROM news_articles ORDER BY cachedAt DESC`).
2. **Background Synchronization:** `refreshHeadlines()` fetches the latest JSON payload from GNews.
3. **Bookmark Preservation:** Before inserting fresh items via `OnConflictStrategy.REPLACE`, existing bookmarked URLs are snapshotted in memory to ensure user-saved articles retain `isBookmarked = true`.
4. **Resilient Failure Mode:** If network fails, the UI gracefully renders stale cached headlines with an interactive retry banner.

---

## 📱 Core Screens & Components

1. **`NewsHomeScreen`**:
   - Custom Broadcast TopAppBar with live notification pill counter.
   - 4-second auto-advancing `StudioBannerCarousel`.
   - Vertical `LazyColumn` of `HeadlineCard` items with smooth animated state transitions (`fadeIn` / `fadeOut`).
   - Custom studio bottom navigation (`Home`, `Categories`, `Saved`, `Profile`).
2. **`NewsDetailScreen`**:
   - URL-encoded type-safe navigation route (`detail/{articleUrl}`).
   - Edge-to-edge header photo with floating `BreakingNewsBadge`.
   - Real-time formatted timestamp chips using ISO-8601 string parsing.
   - Interactive bookmark toggling connected directly to Room database.
   - Horizontal scrolling "Related News" rail.

---

## 🌐 API Integration (GNews REST API)

Configured with the GNews REST endpoint (`https://gnews.io/api/`):

```kotlin
@GET("v4/top-headlines")
suspend fun getTopHeadlines(
    @Query("token")    token: String,
    @Query("lang")     lang: String    = "en",
    @Query("country")  country: String = "in",
    @Query("max")      max: Int        = 10,
    @Query("category") category: String = "general"
): GNewsResponse
```

The API key is injected at compile-time via `BuildConfig.GNEWS_API_KEY`, secured through `gradle.properties` / `local.properties`.

---

## 📁 Directory & File Structure

```
Task-3/
├── app/
│   ├── build.gradle.kts                          # Dependencies, Hilt, Room, KSP, Compose
│   ├── proguard-rules.pro                        # Minification rules for Retrofit, Room, Hilt
│   └── src/main/
│       ├── AndroidManifest.xml                   # Permissions & application entry
│       ├── res/values/
│       │   ├── strings.xml
│       │   └── themes.xml
│       └── java/com/deshnews/app/
│           ├── DeshNewsApplication.kt            # @HiltAndroidApp
│           ├── MainActivity.kt                   # NavHost & edge-to-edge entry point
│           ├── data/
│           │   ├── local/
│           │   │   ├── NewsDao.kt                # Room DAO queries
│           │   │   ├── NewsDatabase.kt           # Room Database class
│           │   │   └── NewsEntity.kt             # Room table entity
│           │   ├── remote/
│           │   │   ├── NewsApiService.kt         # Retrofit API interface
│           │   │   └── dto/NewsDto.kt            # Kotlinx Serializable DTOs
│           │   └── repository/
│           │       └── NewsRepositoryImpl.kt     # Offline-first repository
│           ├── di/
│           │   ├── DatabaseModule.kt             # Room Hilt module
│           │   ├── NetworkModule.kt              # Retrofit & OkHttp Hilt module
│           │   └── RepositoryModule.kt           # Repository binding module
│           ├── domain/
│           │   ├── model/NewsArticle.kt          # Clean domain model
│           │   ├── repository/NewsRepository.kt  # Repository contract
│           │   └── usecase/GetHeadlinesUseCase.kt# Headline fetch use case
│           └── presentation/
│               ├── state/NewsUiState.kt          # Sealed UI states (Home & Detail)
│               ├── viewmodel/NewsViewModel.kt    # MVVM StateFlow ViewModel
│               └── ui/
│                   ├── components/
│                   │   ├── BreakingNewsBadge.kt  # Dual-tone badge & chips
│                   │   ├── HeadlineCard.kt       # Compact headline & related cards
│                   │   └── StudioBannerCarousel.kt# 3D dual-pane stage carousel
│                   ├── screen/
│                   │   ├── NewsDetailScreen.kt   # Article detail view
│                   │   └── NewsHomeScreen.kt     # Main broadcast feed
│                   └── theme/
│                       ├── Color.kt              # Studio broadcast palette
│                       ├── Theme.kt              # Material 3 Dark theme
│                       └── Type.kt               # Typography tokens
├── gradle/
│   └── libs.versions.toml                        # Gradle version catalog
├── build.gradle.kts                              # Root build file
├── gradle.properties                             # Project properties & API key
├── local.properties                              # Local SDK & API key override
└── settings.gradle.kts                           # Module inclusion
```

---

## 🚀 Setup, Build & Execution Guide

### Prerequisites
- **Android Studio:** Hedgehog (2023.1.1) / Ladybug / Iguana or later.
- **JDK:** Java Development Kit 17.
- **Target / Compile SDK:** Android 34 (API Level 34).
- **Minimum OS:** Android 8.0 (API Level 26) or higher.

### Quick Start
1. **Open Project:** Open the `Task-3` directory in Android Studio.
2. **Verify API Key:** Ensure your GNews API Key is added to `local.properties` (this file is git-ignored for security):
   ```properties
   GNEWS_API_KEY=YOUR_ACTUAL_API_KEY_HERE
   ```
3. **Gradle Sync:** Click **Sync Project with Gradle Files** to download all dependencies.
4. **Build & Run:** Select your target emulator or physical Android device and press **`Shift + F10`**.

---

## 👨‍💻 Author & Credits

- **Developer:** Aman Kanojiya
- **Program:** Syntecxhub Internship & Virtual Training Program
- **Repository:** [Syntecxhub-Tasks](https://github.com/codedbyamankanojiya/Syntecxhub-Tasks)

*Built with ❤️ for Modern Android Development.*
