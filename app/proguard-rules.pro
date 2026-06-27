# Preserve line numbers for Play Vitals stack traces.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Room schema and entities.
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep class com.abra.data.local.** { *; }

# PDFBox Android (Java port; keep reflective entry points).
-keep class com.tom_roush.pdfbox.** { *; }
-keep class com.tom_roush.harmony.** { *; }
-dontwarn org.bouncycastle.**
-dontwarn java.awt.**
-dontwarn javax.imageio.**
-dontwarn com.gemalto.jp2.**

# AndroidX Media session compat.
-keep class android.support.v4.media.** { *; }

# Kotlin serialization of enums used across layers.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
