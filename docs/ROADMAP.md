# Roadmap

## Phase 0 — Baseline in WSL+QEMU (1-2 weeks)
- [ ] Run tools/wsl-setup.sh in Ubuntu-22.04 WSL2
- [ ] Clone asteroid build repo, `prepare-build.sh qemux86_64`, `bitbake asteroid-image`
- [ ] Boot in QEMU, record: boot time, idle RAM, qml fps, touch latency
- [ ] File baseline in tools/metrics.md

## Phase 1 — Fork & Optimize launcher+core
- [ ] Fork meta-asteroid, asteroid-launcher, qml-asteroid into HoraOS org
- [ ] systemd preset slim, Qt6 RHI on, QML heavy effects removed
- [ ] MCE suspend tuning, zram on
- [ ] Target: -30% RAM, no dropped frames in launcher swipe

## Phase 2 — Compat layers
- [ ] BLE sync hardening with AsteroidOSync + Gadgetbridge sniff
- [ ] PWA wrapper app template
- [ ] (opt) Waydroid proof-of-concept on qemux86_64 only

## Non-goals for now
- Real Play certification, iOS sync, new watch port, musl switch
