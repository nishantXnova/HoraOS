# Budgets — how we prove EFFICIENT / SMOOTH

## Efficient
- `systemd-analyze` boot <10s in QEMU (stock Asteroid ~15-20s)
- `free -m` base after launcher <200MB (stock ~250-300MB)
- `powertop` wakeups/s <50 idle, suspend/resume works via MCE

## Smooth
- `QSG_RENDER_TIMING=1` avg <16.6ms, p95 <20ms on launcher swipe
- No QML `DropShadow` in hot path, use `layer.enabled: true` + cache
- Compositor vsync locked 60Hz, check `WAYLAND_DEBUG=1` frame callbacks

## Compatible
- `qml-asteroid` demo apps all launch (calendar, music, weather, alarm, calculator)
- BLE: AsteroidOSync connects, notification arrives <2s
- PWA wrapper loads offline page <1s

Record baseline BEFORE optimizing, or you can't claim wins.
