# Contributing to WispOS

Thanks for helping build the lean Wear alternative. Small, focused PRs beat big ones.

## Prereqs
- Android Studio (Ladybug+) with a Wear OS 5+ emulator image
- JDK 17 (the project pins Gradle to it — see `WATCHOSS-aosp/launcher/gradle.properties`)
- The Gradle wrapper in `WATCHOSS-aosp/launcher` (8.10.2, cached after first sync)

## Workflow
1. Fork the repo, branch from `main`: `feat/<short-name>` or `fix/<short-name>`
2. Open `WATCHOSS-aosp/launcher` in Android Studio (New Window), let Gradle sync finish
3. Make your change; keep it to one concern per PR
4. Verify: `./gradlew :app:assembleDebug :app:lintDebug :app:testDebugUnitTest`
   (run from `WATCHOSS-aosp/launcher`)
5. Open a PR with: what changed, which emulator image you tested on, screenshots/video
   for UI changes

## Rules
- No Google Play Services dependencies in the launcher — MicroG/Aurora-compatible only
- No network calls on the UI hot path; cache icons/results aggressively
- Keep the OLED-black theme; light surfaces need a battery justification
- Kotlin style follows `.editorconfig` (2-space indent in this repo)
- New logic needs a unit test under `app/src/test`
- Never commit: keystores, `local.properties`, API keys, `build/` output (see `.gitignore`)

## Reporting bugs
Include: emulator image (e.g. Wear OS 5 x86_64), WispOS version, steps, expected vs
actual, and logcat around the failure.
