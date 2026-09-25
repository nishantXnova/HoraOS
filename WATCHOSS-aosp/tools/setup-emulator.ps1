# WATCHOSS emulator bootstrap — run in normal (non-admin) PowerShell
$Sdk = "$env:LOCALAPPDATA\Android\Sdk"
$env:Path += ";$Sdk\platform-tools;$Sdk\emulator;$Sdk\cmdline-tools\latest\bin"
$Avd = "WATCHOSS_Small_Round_API34"

# 1. Ensure cmdline-tools
if (!(Test-Path "$Sdk\cmdline-tools\latest\bin\sdkmanager.bat")) {
  Write-Host "Install cmdline-tools from Studio > SDK Manager first, then re-run."
  exit 1
}
# 2. Install Wear image (x86_64 fast path)
sdkmanager "system-images;34;android-wear;x86_64" "platforms;android-34"
# 3. Create AVD if missing
if (!(avdmanager list avd | Select-String $Avd)) {
  echo "no" | avdmanager create avd -n $Avd -k "system-images;34;android-wear;x86_64" -d "wearos_small_round"
}
# 4. Launch
emulator -avd $Avd -memory 2048 -gpu swiftshader_indirect -no-snapshot -wipe-data:$false
