# Domain models are Gson-serialized (SharedPreferences) and Parcelable.
-keep class com.advice.core.local.** { *; }

# Gson reflective TypeToken usage.
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type
