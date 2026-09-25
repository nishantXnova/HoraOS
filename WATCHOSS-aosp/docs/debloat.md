# Debloat — keep efficient without losing APK compat

SAFE to disable (recovers battery/RAM, keeps Play-style compat):
```
com.google.android.wearable.assistant # Assistant hotword — biggest idle drain
com.google.android.apps.fitness       # Fit background sync
com.google.android.gms.location.history
com.google.android.play.games
com.google.android.apps.maps # keep only if you need nav, else Aurora Maps PWA
com.google.android.talkback
com.google.android.printservice.recommendation
```

NEVER disable (breaks APK compat/smoothness):
```
com.google.android.gms # GMS core — needed unless you switch to MicroG
com.google.android.gsf
com.android.vending # Play Store / Aurora shim needs framework
com.android.systemui
com.android.launcher # keep until WispOS launcher set default
```

Apply: `tools/debloat.ps1 -Mode safe` measures `adb shell dumpsys batterystats` before/after.
MicroG path (later): replace GMS with `com.microg.gms`, test push + location per-APK.
