Set-Location $PSScriptRoot
Write-Host "Compilando..." -ForegroundColor Cyan
& .\gradlew.bat -q compileKotlin
if ($LASTEXITCODE -ne 0) { Write-Host "Error al compilar"; exit 1 }
$stdlib = Get-ChildItem "$env:USERPROFILE\.gradle\caches\modules-2\files-2.1\org.jetbrains.kotlin\kotlin-stdlib\2.1.20" -Recurse -Filter "kotlin-stdlib-2.1.20.jar" | Select-Object -First 1
Write-Host "Ejecutando programa (escribe cuando te pregunte)..." -ForegroundColor Green
java -cp "build\classes\kotlin\main;$($stdlib.FullName)" MainKt
