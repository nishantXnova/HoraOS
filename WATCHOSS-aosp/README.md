# WispOS (WATCHOSS-aosp) — Minimal AOSP Wear, lean + smooth + compatible (ACTIVE)

## Status: launcher v0.3 runs on emulator ✅
3-screen OS feel: **quick settings | watch face | real app drawer**. Build green (~5s incremental).
Brand: **WispOS**, package `com.wispos.launcher`.
Compat proof: **reReminder (F-Droid) installed, listed in drawer, launches** ✅

## What works now
- Wear OS 5 x86_64 emulator (`WATCHOSS_Small_Round` AVD, API 34+)
- `launcher/` builds: AGP 8.5.2 + Kotlin 1.9.24 + Gradle 8.10.2 + JDK 17 (all pinned, offline-safe)
- Watch face: live ticking clock, date, battery %, app count
- Quick settings: Wi-Fi / Bluetooth / Brightness / Battery tiles → open real system screens
- App drawer: **real installed apps** via PackageManager (labels + icons, sorted, cached), tap to launch
- Manifest: HOME + LAUNCHER intent, `<queries>` block (empty-list fix on API 30+), `singleTask`

## Fixes applied (for the record)
1. Gradle dist download timeout → `gradle-wrapper.properties` pinned to cached 8.10.2
2. Gradle JVM 25 incompatible → `.idea/gradle.xml` + `gradle.properties` pin JDK 17
3. `org.jetbrains.kotlin.plugin.compose:1.9.24` not found → removed (Kotlin-1.9-era Compose needs no such plugin) + `composeOptions.kotlinCompilerExtensionVersion = "1.5.14"`
4. `fillMaxWidth` unresolved → proper `Modifier` imports + `compose.ui:ui` / `foundation-layout` deps
5. Stub list → real `queryIntentActivities` + `getLaunchIntentForPackage` + icon bitmap cache
6. Rename WATCHOSS → HoraOS → WispOS: package, label, project name, docs

## Run
1. Open `WATCHOSS-aosp/launcher` in Android Studio
2. Device → booted `Wear OS Small Round` → ▶ **Run 'app'**
3. Watch → swipe up → tap **WispOS** → swipe left/right between screens

Note: old `com.watchoss.launcher` / `com.horaos.launcher` copies may still sit on the
via watch Settings → Apps once the WispOS build is on.

## Still efficient?
- No GMS calls, no network on hot path, icons decoded once off-main-thread and cached
- Debloat list in `docs/debloat.md` not yet applied — do after feel is right

## Next
- [x] Confirm drawer shows all system apps + launches them (incl. F-Droid proof: reReminder)
- [ ] Scroll-fps check + overdraw pass on round screen
- [ ] Set WispOS as default HOME, test crown/home-button behavior
- [ ] `tools/debloat.ps1` safe pass, measure idle drain
- [ ] Aurora Store APK install test (compat proof)
