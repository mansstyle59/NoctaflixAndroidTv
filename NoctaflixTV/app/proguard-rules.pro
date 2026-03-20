-keep class com.noctaflix.tv.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
