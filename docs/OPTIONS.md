# HoraOS Options — Efficiency dialed down, Compatibility + Smoothness up

Goal shift: still efficient, but prioritize GREAT compat (APK/Play-style) + smooth 60fps.

## Option 1 — Minimal AOSP Wear (recommended for your Galaxy-like goal)
Base: AOSP 13/14 Wear branch + lean launcher, MicroG instead of full GMS.
- Compat: BEST. Runs real APKs natively (ART + Binder). Aurora Store / F-Droid work. Real Play needs certification, MicroG covers 80%.
- Smooth: BEST. SurfaceFlinger + HW composer, mature touch pipeline, GPU drivers as-is.
- Efficient: MODERATE. ~800MB-1.2GB RAM idle, 1-2 day battery vs 3-5 day on Asteroid. Debloat (no Google Assistant hotword, no Fit后台) recovers ~20%.
- Emulator: EXCELLENT. Android Studio Wear emulator on Windows, no Yocto, fast iterate.
- Porting: HARD on locked watches (Galaxy/Pixel locked). Easy on unlockable (TicWatch Pro 3, Fossil Gen5/6, OPPO).
- Work: strip GMS, custom watchface engine, companion BLE sync.

## Option 2 — AsteroidOS + Waydroid container (current fork path)
Base: Asteroid Qt lean + LXC Android 11-13 for APKs only when needed.
- Compat: GOOD when container on, NATIVE QML always. No Play cert, use MicroG/Aurora.
- Smooth: MIXED. Native QML 60fps, APKs janky via Wayland forwarding.
- Efficient: GOOD when container off (<200MB), BAD when on (+800MB).
- Emulator: SLOW. Yocto 2-6h builds, QEMU virgl flaky.
- Porting: MEDIUM. libhybris reuse, same as Asteroid.
- Work: keep docs/ARCHITECTURE.md + add Waydroid card.

## Option 3 — postmarketOS + watch shell (Alpine/mainline)
Base: pmOS + Phosh-derived or custom Qt shell, Waydroid optional same as Opt2.
- Compat: SAME as Opt2 for APKs, BETTER mainline device support than Asteroid.
- Smooth: GOOD. Modern Mesa/Wayland, no libhybris hacks where mainline exists.
- Efficient: GOOD. Alpine musl + OpenRC leaner than Yocto+systemd.
- Emulator: GOOD. pmOS QEMU images, faster than Yocto.
- Porting: BEST long-term (mainline kernel focus).
- Work: write small watch shell from scratch.

## Option 4 — MCU OS (InfiniTime/Zephyr) — NOT for you
PineTime-class. 7-day battery, 64KB RAM, zero APK. Reject per compat requirement.

## Recommendation
You want Galaxy-like apps + smooth + still efficient, emulator-first on Windows:
-> **Option 1, minimal AOSP Wear.**
Reason: only path with true APK compat without dual-OS jank. Efficiency hit is real
but acceptable (1-2 days, still better than stock Wear with debloat).
Keep Asteroid fork as LITE edition later.

Next if you pick Opt1: install Android Studio + Wear OS Small Round API 34 image,
prototype lean launcher (Jetpack Compose for Wear), measure RAM/fps. No WSL/Yocto needed.
Say `go AOSP` and I'll scaffold `WATCHOSS-aosp/` with launcher stub + debloat list + emulator steps.
Say `stay hybrid` and we continue Yocto `emulator` build.
