#!/bin/bash

# Script de diagnóstico para problemas de despliegue
echo "🔍 DIAGNÓSTICO DE PROBLEMAS DE DESPLIEGUE"
echo "========================================"

echo ""
echo "1️⃣ Estado de los contenedores:"
docker-compose ps

echo ""
echo "2️⃣ Logs de la API (últimas 50 líneas):"
echo "----------------------------------------"
docker-compose logs --tail=50 api

echo ""
echo "3️⃣ Logs de MySQL (últimas 20 líneas):"
echo "----------------------------------------"
docker-compose logs --tail=20 mysql

echo ""
echo "4️⃣ Verificando conectividad de red:"
echo "-----------------------------------"
docker network ls | grep panaderia || echo "Red no encontrada"

echo ""
echo "5️⃣ Variables de entorno de la API:"
echo "----------------------------------"
docker exec panaderia-api env | grep -E "(SPRING|MYSQL)" || echo "Contenedor no disponible"

echo ""
echo "6️⃣ Estado de la base de datos:"
echo "------------------------------"
docker exec panaderia-mysql mysql -u root -ppanaderia_root_2024 -e "SHOW DATABASES;" 2>/dev/null || echo "No se puede conectar a MySQL"

echo ""
echo "7️⃣ Verificando si las tablas existen:"
echo "------------------------------------"
docker exec panaderia-mysql mysql -u root -ppanaderia_root_2024 panaderia -e "SHOW TABLES;" 2>/dev/null || echo "Base de datos 'panaderia' no disponible"

echo ""
echo "8️⃣ Espacio en disco:"
echo "-------------------"
df -h

echo ""
echo "9️⃣ Memoria disponible:"
echo "--------------------"
free -h

echo ""
echo "🔧 POSIBLES SOLUCIONES:"
echo "======================"
echo "A. Si hay error de conexión DB: docker-compose restart mysql && sleep 30 && docker-compose restart api"
echo "B. Si hay error de build: docker-compose build --no-cache api && docker-compose up -d api"
echo "C. Si hay problema de permisos: docker exec panaderia-mysql mysql -u root -ppanaderia_root_2024 -e \"GRANT ALL ON panaderia.* TO 'panaderia_user'@'%';\""
echo "D. Para reinicio completo: docker-compose down && docker-compose up -d"