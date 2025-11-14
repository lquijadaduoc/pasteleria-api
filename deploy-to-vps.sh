#!/bin/bash

# Script de despliegue para VPS
# Uso: ./deploy-to-vps.sh [VPS_IP]

VPS_IP=${1:-"168.197.50.14"}
PROJECT_NAME="panaderia-api"

echo "🚀 Iniciando despliegue a VPS: $VPS_IP"

# 1. Preparar archivos locales
echo "📦 Preparando código para despliegue..."
if [ -d "deploy-temp" ]; then
    rm -rf deploy-temp
fi
mkdir deploy-temp

# Copiar archivos necesarios
cp -r src deploy-temp/
cp pom.xml deploy-temp/
cp Dockerfile deploy-temp/
cp docker-compose.production.yml deploy-temp/docker-compose.yml
cp .env.production deploy-temp/.env
cp -r .mvn deploy-temp/
cp mvnw.cmd deploy-temp/ 2>/dev/null || echo "mvnw.cmd no encontrado"

# 2. Crear archivo de despliegue
cat > deploy-temp/deploy.sh << 'EOF'
#!/bin/bash
echo "🔧 Actualizando aplicación en VPS (preservando datos)..."

# Detener solo los contenedores SIN eliminar volúmenes (preservar datos)
docker-compose stop 2>/dev/null || true

# Limpiar solo imágenes no utilizadas (NO volúmenes)
docker image prune -f

echo "💾 Volúmenes de datos preservados..."

# Construir y ejecutar
echo "🏗️ Construyendo aplicación..."
docker-compose build --no-cache

echo "🚀 Iniciando servicios..."
docker-compose up -d

echo "⏳ Esperando que la aplicación esté lista..."
sleep 30

# Verificar estado
echo "📊 Estado de los contenedores:"
docker-compose ps

echo "🧪 Probando conectividad:"
if curl -f http://localhost:8080/api/productos/test 2>/dev/null; then
    echo "✅ API responde correctamente"
    echo "✅ Despliegue completado exitosamente!"
    echo "🌐 API disponible en: http://$(curl -s ifconfig.me):8080"
else
    echo "❌ API no responde - revisando problema..."
    echo ""
    echo "📊 Estado actual de contenedores:"
    docker-compose ps
    echo ""
    echo "⚠️ PROBLEMA DETECTADO. Opciones para solucionarlo:"
    echo "1. Ejecutar diagnóstico: chmod +x diagnostico.sh && ./diagnostico.sh"
    echo "2. Solución automática: chmod +x solucion-rapida.sh && ./solucion-rapida.sh"
    echo "3. Ver logs detallados: docker-compose logs api"
    echo "4. Reinicio completo: docker-compose down && docker-compose up -d"
fi
EOF

chmod +x deploy-temp/deploy.sh

# 3. Comprimir para transferencia
echo "📦 Creando paquete de despliegue..."
cd deploy-temp
tar -czf ../${PROJECT_NAME}-deploy.tar.gz .
cd ..

echo "✅ Paquete creado: ${PROJECT_NAME}-deploy.tar.gz"
echo ""
echo "📋 Para desplegar manualmente en el VPS:"
echo "1. scp ${PROJECT_NAME}-deploy.tar.gz root@${VPS_IP}:/root/"
echo "2. ssh root@${VPS_IP}"
echo "3. cd /root && tar -xzf ${PROJECT_NAME}-deploy.tar.gz"
echo "4. ./deploy.sh"
echo ""
echo "🎯 La API estará disponible en: http://${VPS_IP}:8080"

# Limpiar
rm -rf deploy-temp

echo "🎉 Script completado!"