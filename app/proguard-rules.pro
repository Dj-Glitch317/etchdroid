# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontobfuscate
-keep class java.lang.ClassValue
-dontwarn reactor.blockhound.integration.BlockHoundIntegration
# ── EtchDroid Moto Stylus 5G fixes ──
# Keep libaums USB mass storage classes (required for runtime reflection)
-keep class me.jahnen.libaums.** { *; }
-keep class me.jahnen.libaums.libusbcommunication.** { *; }

# Keep Parcelize-generated CREATOR fields
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# Keep EtchDroid exception hierarchy (serialized in Intents)
-keep class eu.depau.etchdroid.utils.exception.** { *; }

# Prevent stripping of coroutine debug probes
-keep class kotlinx.coroutines.debug.** { *; }
