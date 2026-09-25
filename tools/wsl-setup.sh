#!/bin/bash
# WSL2 Ubuntu 22.04 setup for AsteroidOS/Yocto QEMU builds
set -e
sudo apt update
sudo apt install -y gawk wget git diffstat unzip texinfo gcc build-essential \
  chrpath socat cpio python3 python3-pip python3-pexpect xz-utils debianutils \
  iputils-ping python3-git python3-jinja2 libegl1-mesa libsdl1.2-dev \
  pylint xterm python3-subunit mesa-common-dev zstd liblz4-tool file locales \
  qemu-system-x86 qemu-utils wayland-protocols
sudo locale-gen en_US.UTF-8
git config --global user.name "WATCHOSS" || true
echo "Done. Next: clone asteroid + prepare-build.sh qemux86_64"
echo "Need 100GB free, 16GB RAM allocated to WSL (.wslconfig memory=16GB)."
