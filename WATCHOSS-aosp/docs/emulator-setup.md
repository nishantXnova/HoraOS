# Emulator setup — Wear OS Small Round API 34

Why API 34 Wear OS 4: latest stable round image, matches TicWatch/Fossil Gen6 behavior,
lighter than API 35/36. `android-34` + build-tools `34.0.0` already installed.

Manual fallback if script fails:
```
sdkmanager "system-images;34;android-wear;arm64-v8a" "system-images;34;android-wear;x86_64"
avdmanager create avd -n WATCHOSS_Small_Round_API34 -k "system-images;34;android-wear;x86_64" -d "wearos_small_round"
emulator -avd WATCHOSS_Small_Round_API34 -gpu swiftshader_indirect -no-snapshot -memory 2048
```
x86_64 preferred on Windows for speed (HAXM/WHPX). Use arm64 only for Snapdragon W5 fidelity testing.
