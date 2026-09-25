# WispOS Architecture (Fork of AsteroidOS 2.0)

## Upstream baseline
- Build: OpenEmbedded/Yocto + bitbake `asteroid-image`
- Init: systemd + MCE (Mode Control Entity for suspend/raise-to-wake)
- Graphics: Qt6/QML + Lipstick Wayland compositor (`asteroid-launcher`)
- GPU: libhybris (reuses Android Bionic blob drivers) — main source of jank + porting pain
- BT sync: `asteroid-btsyncd` + BlueZ 5 (BLE GATT to Android AsteroidOSync app)
- Apps: QML + `qml-asteroid` components, SDK via Yocto cross-toolchain

## What WispOS changes (fork-and-optimize)

### 1. Efficient
- `systemd` minimal preset: disable udev bloat, journald volatile, remove ModemManager where unused
- musl evaluation later; first stay on glibc for compat, cut `packagegroup`
- MCE tuning: aggressive suspend, CPU governor `schedutil`, zram for <512MB watches
- Measure: `systemd-analyze`, `memory-report`, `powertop` in QEMU (see tools/metrics.md)

### 2. Smooth
- Keep `asteroid-launcher` but upgrade fully to Qt6 RHI, enable threaded render loop
- Profile QML: `QSG_RENDER_TIMING=1`, replace heavy DropShadow/Blur with cached layers
- Compositor: vsync to 60Hz, triple-buffer, skip libhybris where Mesa Freedreno/Adreno mainline works in QEMU (virgl)
- Touch: libinput low-latency, predictive drag in QML `Flickable`

### 3. Compatible (layered)
- L0: 100% Asteroid QML compat — keep `qml-asteroid` API stable
- L1: Phone sync — BLE notifications/media/weather, Gadgetbridge-friendly GATT
- L2 (experimental, QEMU/high-RAM only): LXC + Waydroid minimal for select APKs. NOT Play Certified, NOT efficient. Disabled by default.
- No fake promise of native Play Store. Needs ART + GMS + 2GB RAM + certification.

## QEMU target first
- Machine: `qemux86_64` with `virgl` + `wayland` for GPU test, `qemuarm64` later for watch-like ARM
- Long term: mainline kernel + Mesa > libhybris blobs where possible
