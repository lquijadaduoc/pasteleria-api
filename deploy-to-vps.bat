@echo off
REM Script de despliegue para Windows
REM Uso: deploy-to-vps.bat [VPS_IP]

set VPS_IP=%1
if "%VPS_IP%"=="" set VPS_IP=168.197.50.14

echo 🚀 Iniciando despliegue a VPS: %VPS_IP%

REM Limpiar directorio temporal
if exist deploy-temp rmdir /s /q deploy-temp
mkdir deploy-temp

REM Copiar archivos necesarios
echo 📦 Preparando archivos...
xcopy src deploy-temp\src\ /s /e /i >nul
copy pom.xml deploy-temp\ >nul
copy Dockerfile deploy-temp\ >nul
copy docker-compose.production.yml deploy-temp\docker-compose.yml >nul
copy .env.production deploy-temp\.env >nul
xcopy .mvn deploy-temp\.mvn\ /s /e /i >nul
copy mvnw.cmd deploy-temp\ >nul 2>nul
xcopy sql deploy-temp\sql\ /s /e /i >nul

REM Copiar scripts de despliegue y diagnóstico
copy deploy-to-vps.sh deploy-temp\deploy.sh >nul
copy deploy-clean.sh deploy-temp\ >nul 2>nul
copy diagnostico.sh deploy-temp\ >nul 2>nul
copy solucion-rapida.sh deploy-temp\ >nul 2>nul

REM Crear archivo comprimido
echo 📦 Creando paquete de despliegue...
cd deploy-temp
powershell -command "Compress-Archive -Path * -DestinationPath ..\panaderia-api-deploy.zip -Force"
cd ..

echo ✅ Paquete creado: panaderia-api-deploy.zip
echo.
echo 📋 Para desplegar manualmente en el VPS:
echo 1. Transferir panaderia-api-deploy.zip al VPS
echo 2. ssh root@%VPS_IP%
echo 3. unzip panaderia-api-deploy.zip
echo 4. chmod +x deploy.sh ^&^& ./deploy.sh
echo.
echo 🎯 La API estará disponible en: http://%VPS_IP%:8080

REM Limpiar
rmdir /s /q deploy-temp

echo 🎉 Script completado!
pause