# WATCHOSS safe debloat — emulator must be booted (`adb devices` shows emulator-5554)
param([string]$Mode = "safe")
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
& $adb devices
$safe = @(
  "com.google.android.wearable.assistant",
  "com.google.android.apps.fitness",
  "com.google.android.play.games",
  "com.google.android.talkback"
)
foreach ($pkg in $safe) {
  Write-Host "Disabling $pkg"
  & $adb shell pm disable-user --user 0 $pkg
}
Write-Host "Done. Check: adb shell dumpsys batterystats | Select-String 'Estimated power'"
