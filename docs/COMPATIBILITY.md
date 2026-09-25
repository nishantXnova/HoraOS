# Compatibility — Galaxy Play Store expectation vs. reality

You asked for: "like Galaxy Watch, handle apps from Play Store really well."

## Truth
- Galaxy Watch = Wear OS = full Android: Linux + Bionic + ART + Binder + Play Services + signed GMS.
- AsteroidOS = embedded Linux: Linux + glibc/musl + systemd + Qt, no ART, no Binder, no Play Services.
- APKs need ART + Android framework. They will not install/run on AsteroidOS natively. Ever, without Android.

## What IS possible
1. **Native compat (do this):** all AsteroidOS QML apps/watchfaces run unmodified. This is your real app store day 1.
2. **Phone-bridged (do this):** notifications, calls, music, weather via BLE from Android phone. Feels like Galaxy for 80% of use.
3. **Web/PWA (cheap win):** tiny WebView wrapper for weather/maps/notes. Huge catalog, low cost.
4. **APK container (experimental):** Waydroid in LXC, Wayland forwarding into launcher card.
   - Needs: 2GB RAM, Binder kernel module, 1GB+ image, Play requires certification (likely MicroG, not real Play).
   - On QEMU: feasible to demo 1-2 APKs. On old watches (512MB): unusable, kills efficiency goal.
   - So: OFF by default, QEMU/high-end only.

## Recommendation for HoraOS
Phase 0-1: nail L0+L1+PWA. Market as "Asteroid-compatible, 2x battery, 60fps."
Phase 2: prove one APK (e.g. Calculator) in Waydroid on QEMU to validate path, then decide if worth it.

If true Play Store is hard requirement, you don't want AsteroidOS fork — you want AOSP Wear port. Say the word and I'll outline that instead (10x heavier).
