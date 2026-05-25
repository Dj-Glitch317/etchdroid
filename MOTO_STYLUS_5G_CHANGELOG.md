# EtchDroid — Moto Stylus 5G 2024 Optimization Changelog

**Device:** Motorola Moto G Stylus 5G (2024)  
**SoC:** Qualcomm Snapdragon 6 Gen 1 (SM6375)  
**Android:** 14 (API 34), pending Android 15  
**USB:** USB-C 2.0 with OTG support  

---

## 🔴 Critical Bug Fix — Build-Breaking

### 1. Missing `UsbDriveTooLargeException.kt` (COMPILATION BLOCKER)
**File:** `app/src/main/java/eu/depau/etchdroid/utils/exception/UsbDriveTooLargeException.kt`

The `WorkerService.kt` imports and throws `UsbDriveTooLargeException` (line 38, 383), but the class file **did not exist** in the repository. This means the project **cannot compile at all** — neither `foss` nor `gplay` variants.

**Fix:** Created the missing exception class extending `FatalException` with proper `@Parcelize` annotation and `getUiMessage()` implementation using the existing `R.string.the_usb_drive_is_too_large` resource.

---

## 🟠 Reliability Fixes — USB Communication

### 2. Integer Overflow in Device Size Calculation
**Files:** `WorkerService.kt`, `BlockDeviceOutputStream.kt`, `BlockDeviceInputStream.kt`

`blockDev.blocks` is a Java `Int` (from libaums). When multiplied by `blockDev.blockSize`, the result can overflow `Int.MAX_VALUE` for any drive larger than ~4GB, producing incorrect sizes. For ≥2TB drives, `blocks` itself goes negative.

**Fix:** All three files now use `(blockDev.blocks.toLong() and 0xFFFFFFFFL)` to treat the block count as an unsigned 32-bit value before multiplication.

### 3. USB Init Retry for Motorola OTG Controllers
**File:** `WorkerService.kt`

The Qualcomm SM6375 USB controller on the Moto Stylus 5G 2024 sometimes needs a second USB mass storage initialization attempt. The first attempt can fail with an `InitException` during SCSI negotiation after an OTG cable is connected.

**Fix:** Added a retry loop (max 2 attempts, 1-second delay between) for the `massStorageDev.init()` call. If the first attempt fails, it logs a warning and retries before propagating the error.

### 4. IO Timeout Increased (10s → 15s)
**File:** `WorkerService.kt`

The default 10-second I/O timeout was too aggressive for the Moto Stylus 5G, which can have slower USB bulk transfer negotiation. Operations would timeout during legitimate long writes.

**Fix:** `IO_TIMEOUT` increased from `10 * 1000L` to `15 * 1000L`.

### 5. USB Device Close Timeout Increased (3s → 5s)
**File:** `WorkerService.kt`

When closing the USB mass storage device after a write, `close()` calls into native libusb code. On Motorola devices (and Samsung/Huawei as noted in original comments), this can take longer than 3 seconds.

**Fix:** `withTimeoutOrNull(3000L)` → `withTimeoutOrNull(5000L)`.

### 6. Timeout Watchdog Logging
**File:** `timeoutWatchdog.kt`

Timeout cancellations were silent, making it hard to debug USB issues from logcat.

**Fix:** Added `Log.e()` with the timeout duration when the watchdog fires, and a descriptive cancellation message.

---

## 🟡 Crash Prevention

### 7. Wakelock Double-Release Guard
**File:** `WorkerService.kt`

On some Moto devices, the system can release the wakelock before the service's `releaseWakelock()` is called, causing an `IllegalStateException` ("WakeLock under-locked").

**Fix:** Added `isHeld` check before calling `release()`, wrapped in try/catch.

### 8. Theme Crash in Non-Activity Contexts
**File:** `Theme.kt`

The `EtchDroidTheme` composable threw an exception if the view context wasn't an `Activity`. This can happen when the theme is evaluated during multi-window transitions or predictive back animations on Android 14+.

**Fix:** Changed `?: throw Exception(...)` to `?: return@SideEffect` to gracefully skip status bar theming when no window is available.

### 9. AsyncStreams Main-Thread Deadlock Protection
**File:** `AsyncStreams.kt`

`runBlocking {}` called on the main thread will deadlock the app. While this shouldn't happen in normal flow, some system callbacks on Motorola's custom framework can trigger it.

**Fix:** Wrapped all `runBlocking` calls in `safeRunBlocking()` which logs a warning with stack trace if called on the main thread, making debugging straightforward.

### 10. ProGuard/R8 Rules for Release Builds
**File:** `proguard-rules.pro`

Release builds with minification could strip libaums classes (loaded via reflection by `UsbCommunicationFactory`), exception classes (serialized in Intent Parcelables), and Parcelize `CREATOR` fields.

**Fix:** Added keep rules for `me.jahnen.libaums.**`, exception hierarchy, Parcelable CREATOR fields, and coroutine debug probes.

---

## 🟢 UX Improvements

### 11. USB Host Feature Relaxed
**File:** `AndroidManifest.xml`

`android.hardware.usb.host` was set to `required="true"`, which prevents the app from appearing in the Play Store on some device variants that report the feature differently.

**Fix:** Changed to `required="false"` with a runtime check in `MainActivity.registerUsbReceiver()` using `PackageManager.FEATURE_USB_HOST`.

### 12. Auto-Launch on USB Connect
**Files:** `AndroidManifest.xml`, `res/xml/usb_device_filter.xml`

The app now auto-launches when a USB mass storage device (class 8) is plugged in, improving the workflow on the Moto Stylus 5G.

### 13. Large Heap Enabled
**File:** `AndroidManifest.xml`

Added `android:largeHeap="true"` to handle large disk images without OOM on the Moto Stylus 5G's 6GB/8GB RAM configurations.

### 14. Null-Safe USB Device List
**File:** `MainActivity.kt`

`usbManager.deviceList` can return `null` on some Motorola firmware versions during early initialization.

**Fix:** `deviceList.values` → `deviceList?.values ?: emptyList()`.

---

## 🔵 Build & Development

### 15. Gradle JVM Memory Increase
**File:** `gradle.properties`

`-Xmx2g` → `-Xmx4g` to prevent OOM during builds with the 25+ translation resource sets.

### 16. Foreground Service Compat Fix
**File:** `Service.kt`

Added explicit `Build.VERSION_CODES.Q` branch for API 29-33 with `@Suppress("DEPRECATION")` annotation for clarity.

### 17. StrictMode in Debug Builds
**File:** `EtchDroidApplication.kt`

Added disk I/O and network violation detection in debug builds for early issue catching.

---

## Summary of Files Changed

| File | Change Type |
|------|-------------|
| `UsbDriveTooLargeException.kt` | **NEW** — missing class (build fix) |
| `usb_device_filter.xml` | **NEW** — USB auto-launch filter |
| `WorkerService.kt` | Modified — 5 fixes |
| `BlockDeviceOutputStream.kt` | Modified — overflow fix |
| `BlockDeviceInputStream.kt` | Modified — overflow fix |
| `AndroidManifest.xml` | Modified — 3 improvements |
| `MainActivity.kt` | Modified — null safety + USB check |
| `Theme.kt` | Modified — crash guard |
| `AsyncStreams.kt` | Modified — deadlock protection |
| `EtchDroidApplication.kt` | Modified — StrictMode |
| `Service.kt` | Modified — compat fix |
| `timeoutWatchdog.kt` | Modified — logging |
| `proguard-rules.pro` | Modified — keep rules |
| `gradle.properties` | Modified — memory |

**Total: 12 files modified, 2 files created, ~131 lines added, ~31 lines removed**
