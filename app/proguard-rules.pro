# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Preserve line numbers for crash reporting / stack traces.
-keepattributes SourceFile,LineNumberTable,Signature

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Navigation destinations / args may be referenced reflectively.
-keep class com.advice.schedule.navigation.** { *; }

# OkHttp optional TLS providers (not on the classpath).
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
