#!/bin/bash

# Script para despliegue COMPLETO (eliminando todos los datos)
# Solo usar cuando quieras empezar desde cero
# Uso: ./deploy-clean.sh

VPS_IP=${1:-"168.197.50.14"}

echo "⚠️  ADVERTENCIA: Este script eliminará TODOS los datos existentes"
echo "🗑️  Se eliminarán: base de datos, pedidos, usuarios, productos"
echo ""
read -p "¿Estás seguro de continuar? (escribe 'SI' para confirmar): " confirmacion

if [ "$confirmacion" != "SI" ]; then
    echo "❌ Operación cancelada"
    exit 1
fi

echo "🚀 Iniciando despliegue LIMPIO a VPS: $VPS_IP"

# Detener y eliminar TODO (incluyendo volúmenes)
docker-compose down -v --remove-orphans 2>/dev/null || true

# Limpiar completamente
docker system prune -af --volumes

echo "🏗️ Construyendo aplicación desde cero..."
docker-compose build --no-cache

echo "🚀 Iniciando servicios con base de datos limpia..."
docker-compose up -d

echo "⏳ Esperando que la aplicación esté lista..."
sleep 45

echo "📊 Estado de los contenedores:"
docker-compose ps

echo "🧪 Probando conectividad:"
curl -f http://localhost:8080/api/productos/test || echo "❌ API no responde"

echo "✅ Despliegue LIMPIO completado!"
echo "🌐 API disponible en: http://$(curl -s ifconfig.me):8080"
echo "🔑 Usuario admin: admin@panaderia.com"