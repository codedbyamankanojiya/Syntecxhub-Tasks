# Add project specific ProGuard rules here.

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Firestore data classes (DTOs) — must not be obfuscated
-keep class com.novachat.app.data.remote.dto.** { *; }
-keepclassmembers class com.novachat.app.data.remote.dto.** {
    <init>();
    <fields>;
}

# Room entities
-keep class com.novachat.app.data.local.entity.** { *; }

# Hilt
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# Kotlin coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Coil
-dontwarn okhttp3.**
-dontwarn okio.**

# Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
