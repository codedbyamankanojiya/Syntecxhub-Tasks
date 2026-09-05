# NovaChat 💬

**Production-ready real-time chat application for Android**, built with Jetpack Compose, Firebase, Room SQLite, and Clean Architecture.

Crafted with a **Warm Minimal** aesthetic — clean layouts, soft natural tones, deep teal branding, cyan-blue read indicators, and coral accents. Zero XML layouts. Pure Jetpack Compose with Material 3.

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
| `Accent` | `#E85D4A` | Coral — unread message pills, navigation badges, error highlights |
| `ReadReceiptBlue` | `#0288D1` | Cyan-Blue — Seen / Read double-check marks (`✓✓`) |
| `TextPrimary` | `#1A1A1A` | Main text & titles |
| `TextSecondary` | `#7A7A7A` | Timestamps, subtitles & secondary labels |
| `Online` | `#4CAF50` | Real-time presence indicator dot |
| `Divider` | `#E8E5E0` | Subtle item dividers & borders |

---

## 📱 Production Features

- **🚀 Animated Splash & Session Gatekeeper**: Fluid animated onboarding sequence with automated Firebase auth verification routing authenticated users directly to `Main` and new users to `Auth`.
- **🔐 Pure Email/Password Authentication**: High-security user onboarding and sign-in with live email/password validations, dynamic show/hide password toggles, clear error prompts, and zero anonymous/guest loopholes.
- **🔑 Forgot Password Reset Flow**: Integrated password reset dialog that dispatches an official Firebase password recovery link directly to the user's email with confirmation feedback.
- **🛡️ Account Security & Credential Management**:
  - In-app **Change Password** requiring current password re-authentication before committing new credentials.
  - In-app **Change Email** with credential re-authentication, updating both Firebase Auth and the Firestore user profile.
- **🗑️ Account Deletion & Data Purge**:
  - Full account deletion purging the user profile document from Firestore, removing active presence, deleting the Firebase Auth record, and redirecting cleanly to the Sign-In screen.
  - **Graceful Disabled User State**: If a chat partner has deleted their account, the chat header and conversation preview seamlessly display `"Deleted User"` with a muted avatar, and the message composition bar is cleanly disabled with a polite explanation bar.
- **🎭 12-Avatar Customization Engine**:
  - Pre-configured vector avatar library with 12 distinctive personalities, diverse styles, and vibrant palettes.
  - Interactive avatar selection bottom sheet enabling instant 1-tap switching without requiring external gallery storage permissions or cloud upload latencies.
  - Avatars and initials sync instantly across Firestore documents and cache in Room.
- **🔄 Global Real-Time Name Synchronization**: Updating display name in Profile immediately updates the Firestore user document, instantly propagating across all active chat headers, participant records, and conversation lists.
- **✓✓ 3-Stage WhatsApp-Style Read Receipts**:
  - **Single White Tick (`✓`)**: Message sent and safely persisted in Cloud Firestore.
  - **Double White Ticks (`✓✓`)**: Message delivered to recipient's client device.
  - **Double Cyan-Blue Ticks (`✓✓` `#0288D1`)**: Message opened and read by recipient in the active chat room.
- **🔔 Unread Alert Blips & Push Notifications**:
  - **Avatar Blip Dot**: Vivid green/cyan dot on conversation avatars indicating unread messages.
  - **Bold Typography & Pill Badges**: Unread chats feature bold headlines, bold message snippets, and coral counter pills.
  - **Bottom Navigation Badge**: Chat icon in the main navigation bar displays the aggregate count of all unread messages.
  - **Android Push & Local Notifications**: High-importance notification channel with heads-up banners, sound, vibration, and single-tap deep-link intent routing straight to the sender's chat room.
- **💬 Real-Time 1-on-1 Messaging**: Sub-second instant messaging powered by Cloud Firestore snapshot listeners paired with background Room SQLite database caching for instant cold starts.
- **📦 Offline-First Persistence**: Instant cold-start loading of messages directly from Room SQLite database before hydrating against remote Firestore.
- **🔍 User Discovery & Instant Search**: Debounced real-time user discovery filtering registered profiles by display name or email.
- **⌨️ Live Typing Engine**: Real-time typing notification in the chat toolbar with automatic 2-second debounce timer to prevent network flooding.
- **🟢 Real-Time Presence System**: Real-time user online/offline status synchronized with the Android Activity lifecycle.
- **🫧 Cluster-Aware Message Bubbles**: Modern rounded message bubble geometry that visually groups consecutive messages from the same sender.
- **🛡️ Hardened Zero-Leak Credential Architecture**: Comprehensive `.gitignore` protecting live API keys, `google-services.json`, and keystores with a safe `google-services.json.example` template.

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
│   │   │   │   └── repository/                 ← ChatRepositoryImpl (Firestore + Room Sync)
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
│   │   │   │   └── ui/                         ← UI Components, Theme, Shapes & Utilities
│   │   │   │       ├── shape/                  ← TelegramBubbleShape (Cluster-aware)
│   │   │   │       ├── theme/                  ← Theme.kt (Tokens, Typography, Dimens)
│   │   │   │       └── util/                   ← AvatarHelper.kt, NotificationHelper.kt
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
   - **Authentication**: Enable **Email/Password** provider (Ensure Anonymous is disabled).
   - **Cloud Firestore**: Create database (start in **Test mode** or apply security rules below).

### 2. Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users: readable by authenticated users, writable & deletable by profile owner
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write, delete: if request.auth.uid == userId;
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

---

## 🚀 Deployment & Release Guide

NovaChat is configured with production-grade R8 code minification, resource shrinking, and ProGuard optimization.

### 1. Build Debug APK (Local Testing)

```bash
# Navigate to the Task-4 directory
cd Task-4

# Build debug APK
./gradlew assembleDebug

# Output APK path:
# app/build/outputs/apk/debug/NovaChat.apk
```

### 2. Build Production Release APK / Android App Bundle (AAB)

For distribution on Google Play or internal enterprise track:

```bash
# Generate signed/unsigned release APK
./gradlew assembleRelease

# Generate Android App Bundle (AAB) for Google Play
./gradlew bundleRelease

# Outputs:
# APK: app/build/outputs/apk/release/app-release-unsigned.apk
# AAB: app/build/outputs/bundle/release/app-release.aab
```

### 3. Production Keystore Setup (Signing)

To sign release builds for distribution:

1. **Generate a keystore**:
   ```bash
   keytool -genkey -v -keystore novachat-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias novachat
   ```

2. **Configure `app/build.gradle.kts`** using environment variables or a git-ignored `keystore.properties`:
   ```kotlin
   signingConfigs {
       create("release") {
           storeFile = file(System.getenv("KEYSTORE_PATH") ?: "novachat-release.jks")
           storePassword = System.getenv("KEYSTORE_PASSWORD")
           keyAlias = System.getenv("KEY_ALIAS") ?: "novachat"
           keyPassword = System.getenv("KEY_PASSWORD")
       }
   }
   buildTypes {
       release {
           signingConfig = signingConfigs.getByName("release")
           isMinifyEnabled = true
           isShrinkResources = true
           proguardFiles(
               getDefaultProguardFile("proguard-android-optimize.txt"),
               "proguard-rules.pro"
           )
       }
   }
   ```

### 4. Pre-Deployment Verification Checklist

- [x] **Zero Secret Leaks**: `google-services.json`, `local.properties`, and keystore files excluded in `.gitignore`.
- [x] **R8 & ProGuard**: Verified with keep-rules for Firebase DTOs, Room Entities, Hilt, and Kotlinx Serialization.
- [x] **Android 13+ Notification Runtime Permission**: Handled in `MainActivity` with high-importance channel creation.
- [x] **Cloud Firestore Rules**: Applied with owner-authenticated read/write/delete constraints.
- [x] **Offline Cache Migration**: Room database versioned with automated fallback/migration strategies.

---

## 📂 File Index

| File | Description |
|------|-------------|
| `SplashScreen.kt` | Session-aware animated onboarding and authentication gatekeeper |
| `AuthScreen.kt` | Warm minimal Sign-In, Sign-Up, and Forgot Password UI |
| `MainScreen.kt` | Inset-safe bottom navigation hosting `Chats` and `Profile` tabs with badge support |
| `ChatListScreen.kt` | Live conversation list with unread blip dots, presence indicators, unread pills, and search |
| `UserSearchScreen.kt` | Real-time user discovery screen with debounced search query execution |
| `ChatRoomScreen.kt` | Real-time conversation view with dynamic bubble grouping, 3-stage ticks, and typing bar |
| `ProfileScreen.kt` | User profile management, 12-avatar grid picker, change password, change email, and delete account |
| `AvatarHelper.kt` | 12-avatar vector generator with distinct palettes, skins, and styles |
| `NotificationHelper.kt` | High-importance heads-up notification channel and banner dispatcher |
| `TelegramBubbleShape.kt` | Cluster-aware message bubble geometry with dynamic corner radiuses |
| `Theme.kt` | Complete design tokens (`NovaChatColors`, `NovaChatTypography`, `NovaChatDimens`) |
| `FirestoreChatService.kt` | Real-time snapshot listeners for chats, messages, presence, read receipts, and typing status |
| `ChatRepositoryImpl.kt` | Offline-first repository fusing Firestore, Room Database, and real-time synchronisation |
| `NovaChatNavGraph.kt` | Declarative Compose navigation graph with type-safe route parameters |

---

## 👨‍💻 Author & Credits

- **Developer:** Aman Kanojiya
- **Program:** Syntecxhub Internship & Virtual Training Program
- **Repository:** [Syntecxhub-Tasks](https://github.com/codedbyamankanojiya/Syntecxhub-Tasks)

*Built with ❤️ for the Syntecxhub Internship Program*
