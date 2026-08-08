# SyncTask - Advanced Android Task Management

**SyncTask** is a production-ready To-Do & Task Management application built with a focus on **Clean Architecture**, **MVVM**, and **Modern Android Development (MAD)** practices.

## 🚀 Key Features
- **Clean Architecture**: Decoupled Domain, Data, and Presentation layers.
- **Jetpack Compose**: 100% declarative UI with Material 3 components.
- **Offline Persistence**: Room database for seamless task management without internet.
- **Brand Identity**: Custom branding with a unique adaptive icon and dynamic theme.
- **Micro-Interactions**: Swipe-to-delete gestures and smooth list animations.
- **Advanced Filtering**: Real-time search and priority-based task filtering.

## 🛠 Tech Stack
- **Language**: Kotlin (2.0.0)
- **UI Framework**: Jetpack Compose (Material 3)
- **Asynchronous**: Kotlin Coroutines & Flow
- **Dependency Injection**: Manual Provider Pattern (Ready for Hilt integration)
- **Local Database**: Room Persistence Library
- **Annotation Processing**: KSP (Kotlin Symbol Processing)
- **Build System**: Gradle 9.1 (Kotlin DSL)


## 📦 Download APK

You can download the latest production-ready APK from the **[Releases](https://github.com/codedbyamankanojiya/Syntecxhub-Tasks/releases)** section of this repository.

1.  Navigate to the [Releases](https://github.com/codedbyamankanojiya/Syntecxhub-Tasks/releases) page.
2.  Download the `app-debug.apk` file.
3.  Install it on your Android device (ensure "Install from Unknown Sources" is enabled).

## 🏗 Project Structure
```text
com.syntecxhub.taskmanagement
├── data          # Data Layer: Room Database, Entities, Daos, Repositories Impl
├── domain        # Domain Layer: Use Cases, Repository Interfaces, Models
└── presentation  # Presentation Layer: ViewModels, Compose UI, Theme
```

## 👨‍💻 Installation
1. Clone the repository.
2. Open in Android Studio (Ladybug or later).
3. Build and run on an emulator or physical device.

---
Built with ❤️ by Aman Kanojiya for Syntecxhub
