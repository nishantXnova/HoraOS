#!/bin/bash
# Runs INSIDE Ubuntu WSL2 — builds stock AsteroidOS 2.0 for `emulator` machine
# Do NOT build under /mnt/c (slow + OneDrive sync). Builds in ~/asteroid.
set -e
sudo apt update && sudo apt upgrade -y
sudo apt install -y git build-essential cpio diffstat gawk file chrpath texinfo \
  python3 python3-packaging python3-setuptools wget shared-mime-info zstd \
  liblz4-tool locales qemu-system-x86 qemu-utils wayland-protocols
sudo locale-gen en_US.UTF-8

mkdir -p ~/asteroid && cd ~/asteroid
if [ ! -f prepare-build.sh ]; then
  git clone --branch 2.0 https://github.com/AsteroidOS/asteroid.git .
fi
source ./prepare-build.sh emulator
# Baseline build — takes 2-6h first time, ~100GB disk
bitbake asteroid-image
echo "BUILD DONE. Run: runqemu asteroid-image"
echo "Then record metrics per tools/metrics.md"
