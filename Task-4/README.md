# NovaChat 💬

**Production-ready real-time chat application for Android**, built with Jetpack Compose, Firebase, Room SQLite, and Clean Architecture.

Crafted with a **Warm Minimal** aesthetic — clean layouts, soft natural tones, deep teal branding, and coral accents. Zero XML layouts. Pure Jetpack Compose with Material 3.

---

## 🎨 Design Language & Color Palette ("Warm Minimal")

NovaChat avoids generic dark/neon styling in favor of a human, approachable, and tactile look:

| Token | Hex | Usage |
|-------|-----|-------|
| `Background` | `#FAF9F6` | Warm off-white screen canvas |
| `Surface` | `#FFFFFF` | Cards, top bars, bottom navigation bar, input bar |
| `SurfaceVariant` | `#F0EDEA` | Incoming message bubbles, search input chips |
| `InputBackground` | `#F5F3F0` | Text input fields & message entry box |
| `Primary` | `#1A6B5C` | Deep Teal — branding headers, active tabs, primary action buttons |
| `PrimaryContainer` | `#1A8D7F` | Lighter Teal — outgoing message bubbles |
| `Accent` | `#E85D4A` | Coral — unread message pills, recording indicators, highlights |
| `TextPrimary` | `#1A1A1A` | Main text & titles |
| `TextSecondary` | `#7A7A7A` | Timestamps, subtitles & secondary labels |
| `Online` | `#4CAF50` | Real-time presence indicator dot |
| `Divider` | `#E8E5E0` | Subtle item dividers & borders |

---

## 📱 Production Features

- **🚀 Animated Splash & Session Routing**: Smooth animated app launch sequence with automatic authentication state verification routing returning users straight to `Main` or onboarding guests/new users to `Auth`.
- **🔐 User Authentication**: Secure Email/Password Sign-Up & Sign-In, plus 1-tap Anonymous Guest access powered by Firebase Authentication with input validations and state feedback.
- **🧭 Bottom Navigation Shell (`MainScreen`)**: Inset-safe, fluid bottom navigation between `Chats` and `Profile & Settings` with animated crossfade page transitions.
- **💬 Real-Time 1-on-1 Messaging**: Sub-second messaging backed by Cloud Firestore real-time snapshot listeners combined with background Room cache synchronisation.
- **📦 Offline-First Persistence**: Instant cold-start loading of messages directly from Room SQLite database before hydrating against remote Firestore.
- **🔍 User Discovery & Search**: Instant debounced user search querying registered profiles by display name or email to initiate new conversations immediately.
- **🎙️ Voice Notes & Audio Waveforms**: Built-in audio capture (`MediaRecorder`), real-time 40-bar amplitude waveform visualization, interactive playback scrub slider, and audio playback via `MediaPlayer`.
- **📷 Media Sharing & Attachments**: Image sharing uploaded directly to Firebase Storage with progressive Coil image rendering, accompanied by a 2×2 `AttachmentBottomSheet`.
- **⌨️ Live Typing Indicators**: Real-time typing notification in the chat toolbar with automatic 2-second debounce timer to prevent network flooding.
- **🟢 Real-Time Presence System**: Real-time user online/offline status synchronized with the Android Activity lifecycle.
- **👤 Profile & Settings Suite**:
  - Profile photo picker with camera/gallery support and direct Firebase Storage sync.
  - Editable display name and personal bio ("About") with change detection.
  - Preference controls: Push notifications toggle.
  - Privacy controls: Read receipts toggle, Last Seen visibility, and Bio ("About") visibility.
  - Account actions: Safe sign-out with confirmation dialog and account overview.
- **🫧 Cluster-Aware Message Bubbles**: Modern rounded message bubble geometry that visually groups consecutive messages from the same sender.
- **🛡️ Zero-Leak Credential Architecture**: Comprehensive root and subfolder `.gitignore` configuration ensuring live API keys and `google-services.json` are excluded from version control, accompanied by a safe `google-services.json.example` template.

---

## 🏗️ Architecture

NovaChat follows **Clean Architecture** with **Unidirectional Data Flow (UDF)**:

```
Task-4/
├── app/
│   ├── src/main/
│   │   ├── java/com/novachat/app/
│   │   │   ├── domain/                         ← Enterprise Business Logic (Pure Kotlin)
│   │   │   │   ├── model/                      ← Chat, Message, User, MessageType
│   │   │   │   ├── repository/                 ← ChatRepository interface
│   │   │   │   └── usecase/                    ← ObserveMessagesUseCase, SendTextMessageUseCase
│   │   │   │
│   │   │   ├── data/                           ← Data Layer (Remote + Local Fusion)
│   │   │   │   ├── remote/                     ← FirestoreChatService (callbackFlow listeners)
│   │   │   │   │   └── dto/                    ← MessageDto, UserDto
│   │   │   │   ├── local/                      ← NovaChatDatabase (Room SQLite Cache)
│   │   │   │   │   ├── dao/                    ← MessageDao
│   │   │   │   │   ├── entity/                 ← MessageEntity
│   │   │   │   │   └── converter/              ← RoomTypeConverters
│   │   │   │   └── repository/                 ← ChatRepositoryImpl (Firestore + Room + Storage)
│   │   │   │
│   │   │   ├── presentation/                   ← UI Layer (100% Jetpack Compose M3)
│   │   │   │   ├── splash/                     ← SplashScreen
│   │   │   │   ├── auth/                       ← AuthScreen + AuthViewModel
│   │   │   │   ├── main/                       ← MainScreen (Bottom Navigation Shell)
│   │   │   │   ├── chatlist/                   ← ChatListScreen + ChatListViewModel
│   │   │   │   ├── search/                     ← UserSearchScreen + UserSearchViewModel
│   │   │   │   ├── chatroom/                   ← ChatRoomScreen + ChatRoomViewModel + UiState
│   │   │   │   ├── profile/                    ← ProfileScreen + ProfileViewModel
│   │   │   │   ├── navigation/                 ← NovaChatNavGraph + NavDestination
│   │   │   │   └── ui/                         ← UI Components, Theme & Shapes
│   │   │   │       ├── component/              ← VoiceNoteWaveform, AttachmentBottomSheet
│   │   │   │       ├── shape/                  ← TelegramBubbleShape (Cluster-aware)
│   │   │   │       └── theme/                  ← Theme.kt (Tokens, Typography, Dimens)
│   │   │   │
│   │   │   ├── di/                             ← Dagger Hilt Dependency Injection Modules
│   │   │   ├── MainActivity.kt                 ← Single Activity entry point
│   │   │   └── NovaChatApplication.kt          ← Application lifecycle & Hilt container
│   │   │
│   │   ├── AndroidManifest.xml
│   │   └── google-services.json.example        ← Template for Firebase configuration
│   │
│   ├── build.gradle.kts
│   └── proguard-rules.pro
│
├── .gitignore                                  ← Production-grade secrets & caches filter
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## ⚙️ Setup & Configuration Instructions

### 1. Firebase Project Setup

1. Open [Firebase Console](https://console.firebase.google.com) and create a new project.
2. Add an Android app with the package name:
   ```
   com.novachat.app
   ```
3. Download `google-services.json` and copy it to:
   ```
   Task-4/app/google-services.json
   ```
   *(A reference template is available at [`Task-4/app/google-services.json.example`](./app/google-services.json.example)).*

4. Enable services in Firebase Console:
   - **Authentication**: Enable **Email/Password** and **Anonymous** sign-in providers.
   - **Cloud Firestore**: Create database (start in **Test mode** or apply security rules below).
   - **Firebase Storage**: Create standard storage bucket (start in **Test mode** or apply rules below).

### 2. Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users: readable by authenticated users, writable by profile owner
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth.uid == userId;
    }

    // Chats: accessible only to participants
    match /chats/{chatId} {
      allow read, write: if request.auth != null && request.auth.uid in resource.data.participantIds;
      allow create: if request.auth != null;

      match /messages/{messageId} {
        allow read, write: if request.auth != null;
      }
    }

    // Typing indicators: accessible to authenticated participants
    match /typing/{docId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### 3. Firebase Storage Rules

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /voice_notes/{chatId}/{fileName} {
      allow read, write: if request.auth != null;
    }
    match /images/{chatId}/{fileName} {
      allow read, write: if request.auth != null;
    }
    match /avatars/{userId}/{fileName} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### 4. Build & Run

```bash
# Navigate to the Task-4 directory
cd Task-4

# Build debug APK
./gradlew assembleDebug

# Output APK path:
# app/build/outputs/apk/debug/NovaChat.apk
```

---

## 📂 File Index

| File | Description |
|------|-------------|
| `SplashScreen.kt` | Session-aware animated onboarding and authentication gatekeeper |
| `AuthScreen.kt` | Warm minimal Sign-In, Sign-Up, and Guest authentication UI |
| `MainScreen.kt` | Inset-safe bottom navigation hosting `Chats` and `Profile` tabs |
| `ChatListScreen.kt` | Live conversation list with presence indicators, unread counts, and search |
| `UserSearchScreen.kt` | Real-time user discovery screen with debounced search query execution |
| `ChatRoomScreen.kt` | Real-time conversation view with dynamic bubble grouping, audio playback, and typing bar |
| `ProfileScreen.kt` | User profile management, avatar photo upload, preferences, privacy toggles, and sign-out |
| `VoiceNoteWaveform.kt` | 40-bar dynamic amplitude audio waveform with interactive scrub track |
| `AttachmentBottomSheet.kt` | 2×2 attachment modal sheet (Gallery, Camera, File, Location) |
| `TelegramBubbleShape.kt` | Cluster-aware message bubble geometry with dynamic corner radiuses |
| `Theme.kt` | Complete design tokens (`NovaChatColors`, `NovaChatTypography`, `NovaChatDimens`) |
| `FirestoreChatService.kt` | Real-time snapshot listeners for chats, messages, presence, and typing status |
| `ChatRepositoryImpl.kt` | Offline-first repository fusing Firestore, Firebase Storage, and Room Database |
| `NovaChatNavGraph.kt` | Declarative Compose navigation graph with type-safe route parameters |

---
## 👨‍💻 Author & Credits

- **Developer:** Aman Kanojiya
- **Program:** Syntecxhub Internship & Virtual Training Program
- **Repository:** [Syntecxhub-Tasks](https://github.com/codedbyamankanojiya/Syntecxhub-Tasks)

*Built with ❤️ for the Syntecxhub Internship Program*
