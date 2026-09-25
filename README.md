# WispOS — a lean, smooth, compatible Wear OS alternative

![android-ci](https://github.com/nishantXnova/WispOS/actions/workflows/android-ci/badge.svg)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

WispOS is a minimal-Wear launcher and watch experience: a live watch face, quick
settings, and a real app drawer — no Assistant hotword, no Fit background drain,
no Play Services dependency. Third-party APKs (F-Droid, Aurora Store) install,
list, and launch from it. Proven on the Wear OS emulator with `reReminder`.

> Origin: the project started as an AsteroidOS 2.0 fork study (`docs/`, `upstream/`
> are that research). It pivoted to minimal AOSP Wear because only ART runs real
> APKs — see `docs/OPTIONS.md` and `docs/COMPATIBILITY.md`.

## Install & run (5 min)

Prereqs: Android Studio (Ladybug+), JDK 17, a Wear OS 5+ emulator image.

1. Clone: `git clone https://github.com/nishantXnova/WispOS.git`
2. Open `WATCHOSS-aosp/launcher` in Android Studio (New Window), wait for Gradle sync
   (Gradle 8.10.2 + JDK 17 are pinned — see `gradle.properties`)
3. Boot a **Wear OS Small Round** emulator (Device Manager)
4. Select device → ▶ **Run 'app'**
5. On the watch: swipe up → tap **WispOS** → swipe left/right: settings | face | apps

## Verify from source

```bash
cd WATCHOSS-aosp/launcher
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

Or `docker build -t wispos .` then `docker run --rm -v "%CD%:/wispos" wispos`
for the same build in a clean room. CI runs all three checks on every push/PR.

## Layout

```
WATCHOSS-aosp/launcher/   # WispOS app (package com.wispos.launcher)
docs/                     # architecture, compatibility, options, roadmap
tools/                    # emulator + debloat helpers
upstream/                 # stock AsteroidOS reference (git-ignored, see docs)
.github/workflows/       # CI: build + tests + lint + debug APK
Dockerfile                # reproducible build image
```

## Progress

10 of 13 milestones done — face/settings/real drawer live, F-Droid proof passed
(`reReminder` installs, lists, launches). Next: default-HOME behavior, fps pass,
debloat + idle-drain measure. Full log in earlier commits; live status in
`WATCHOSS-aosp/README.md`.

## Contributing / conduct / license

- `CONTRIBUTING.md` — branch flow, style, test rules
- `CODE_OF_CONDUCT.md` — Contributor Covenant
- `LICENSE` — MIT
