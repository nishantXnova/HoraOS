# BUILD-QEMU — emulator-first baseline

Target machine is `emulator` (from upstream `prepare-build.sh` devices list),
not `qemux86_64`. Corrected 2026-09-24.

## 1. Install WSL (Admin PowerShell)
```
Set-ExecutionPolicy Bypass -Scope Process
C:\Users\paude\OneDrive\Documents\WATCHOSS\tools\install-wsl.ps1
```
Reboot. Create `.wslconfig` with 16GB RAM.

Free space check: you have ~94GB free. Yocto wants 100GB+. Free 20GB+ extra
or build will fail mid-way. And NEVER build under OneDrive — use `~/asteroid`
inside WSL ext4.

## 2. Build (Ubuntu WSL shell)
```
bash /mnt/c/Users/paude/OneDrive/Documents/WATCHOSS/tools/build-qemu.sh
```

## 3. Run + measure
```
runqemu asteroid-image
systemd-analyze
free -m
QSG_RENDER_TIMING=1 asteroid-launcher
```

Upstream kept at `WATCHOSS/upstream/asteroid` for reference. Fork comes after baseline.
