@echo off
cd /d "%~dp0"
call gradlew.bat -q compileKotlin
if errorlevel 1 (
  echo Error al compilar
  exit /b 1
)
for /f "delims=" %%i in ('dir /s /b "%USERPROFILE%\.gradle\caches\modules-2\files-2.1\org.jetbrains.kotlin\kotlin-stdlib\2.1.20\kotlin-stdlib-2.1.20.jar" 2^>nul') do set STDLIB=%%i
echo Ejecutando programa...
java -cp "build\classes\kotlin\main;%STDLIB%" MainKt
