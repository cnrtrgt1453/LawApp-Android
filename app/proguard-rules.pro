# Kotlinx Serialization Rules
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep serializable classes and their companion objects/fields
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
-keep class * implements kotlinx.serialization.KSerializer {
    *;
}
-keepclassmembers class * {
    *** Companion;
}

# Ktor rules
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**

# Hilt / Dagger rules (usually handled by Hilt libraries, but added for safety)
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**

# Keep our DTO models from being obfuscated (to prevent serialization issues)
-keep class com.lawapp.android.data.model.** { *; }
-keepclassmembers class com.lawapp.android.data.model.** { *; }
