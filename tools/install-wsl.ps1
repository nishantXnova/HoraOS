#Requires -RunAsAdministrator
# Run in Admin PowerShell: Set-ExecutionPolicy Bypass -Scope Process; .\tools\install-wsl.ps1
wsl --install -d Ubuntu-24.04
Write-Host "Reboot, then open Ubuntu-24.04 and run: bash /mnt/c/Users/paude/OneDrive/Documents/WATCHOSS/tools/build-qemu.sh"
Write-Host "TIP: give WSL 16GB in %USERPROFILE%\.wslconfig :"
Write-Host "[wsl2]`nmemory=16GB`nprocessors=8`nswap=8GB"
