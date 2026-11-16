@echo off
chcp 65001 >nul
cd /d "%~dp0"
title Irani Youtuber - Mod Builder
echo ==========================================
echo   Irani Youtuber mod builder
echo ==========================================
echo.

where java >nul 2>nul
if errorlevel 1 (
  echo [ERROR] Java was not found on this computer.
  echo Install JDK 21 from https://adoptium.net  ^(Temurin 21, Windows x64, .msi^)
  echo Then close this window and double-click build.bat again.
  echo.
  pause
  exit /b 1
)

echo Your Java version:
java -version
echo.
echo Building the mod. The first time takes 5-15 minutes and needs internet.
echo DO NOT CLOSE THIS WINDOW.
echo.

powershell -NoProfile -Command "& { .\gradlew.bat build --console=plain 2>&1 | Tee-Object -FilePath build-log.txt }"

echo.
if exist "build\libs\irani-youtuber-1.0.0.jar" (
  if not exist "READY-MOD" mkdir "READY-MOD"
  copy /Y "build\libs\irani-youtuber-1.0.0.jar" "READY-MOD\" >nul
  echo ==========================================
  echo   DONE! Your mod file is in the READY-MOD folder:
  echo   READY-MOD\irani-youtuber-1.0.0.jar
  echo   Put it in  %%appdata%%\.minecraft\mods  together with Fabric API.
  echo ==========================================
  start "" "READY-MOD"
) else (
  echo ==========================================
  echo   The build did NOT finish. No jar was made.
  echo   Send the file  build-log.txt  ^(in this folder^) or a screenshot of this window to Claude.
  echo ==========================================
)
echo.
pause
