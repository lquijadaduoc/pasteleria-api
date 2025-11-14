#!/bin/bash

# Script de solución rápida para problemas comunes de despliegue
echo "🛠️ SOLUCIONADOR AUTOMÁTICO DE PROBLEMAS"
echo "======================================"

function fix_database_connection() {
    echo "🔧 Solucionando problemas de conexión de base de datos..."
    
    # Reiniciar MySQL primero
    docker-compose restart mysql
    echo "⏳ Esperando que MySQL esté listo..."
    sleep 30
    
    # Verificar que MySQL esté funcionando
    docker exec panaderia-mysql mysql -u root -ppanaderia_root_2024 -e "SELECT 1;" 2>/dev/null
    if [ $? -eq 0 ]; then
        echo "✅ MySQL está funcionando"
        
        # Verificar que la base de datos existe
        docker exec panaderia-mysql mysql -u root -ppanaderia_root_2024 -e "SHOW DATABASES LIKE 'panaderia';" 2>/dev/null
        if [ $? -eq 0 ]; then
            echo "✅ Base de datos 'panaderia' existe"
        else
            echo "⚠️ Creando base de datos..."
            docker exec panaderia-mysql mysql -u root -ppanaderia_root_2024 -e "CREATE DATABASE IF NOT EXISTS panaderia;"
        fi
        
        # Reiniciar la API
        docker-compose restart api
        echo "⏳ Esperando que la API arranque..."
        sleep 60
        
    else
        echo "❌ MySQL no responde. Intentando reinicio completo..."
        docker-compose down
        docker-compose up -d mysql
        sleep 45
        docker-compose up -d api
    fi
}

function fix_build_problems() {
    echo "🔧 Solucionando problemas de compilación..."
    
    # Rebuild desde cero
    docker-compose stop api
    docker-compose build --no-cache api
    docker-compose up -d api
    
    echo "⏳ Esperando que la aplicación compile y arranque..."
    sleep 90
}

function check_logs_and_suggest() {
    echo "📋 Revisando logs para diagnosticar..."
    
    # Obtener últimos logs de la API
    api_logs=$(docker-compose logs --tail=20 api 2>/dev/null)
    
    if echo "$api_logs" | grep -i "connection.*refused\|connect.*failed"; then
        echo "🔍 Detectado: Problema de conexión de base de datos"
        fix_database_connection
    elif echo "$api_logs" | grep -i "compilation.*error\|build.*failed"; then
        echo "🔍 Detectado: Problema de compilación"
        fix_build_problems
    elif echo "$api_logs" | grep -i "port.*already.*use\|bind.*failed"; then
        echo "🔍 Detectado: Puerto ocupado"
        echo "🔧 Matando procesos en puerto 8080..."
        docker-compose down
        sleep 10
        docker-compose up -d
    else
        echo "🔍 No se detectó un error específico. Aplicando solución general..."
        docker-compose down
        sleep 10
        docker-compose up -d
        echo "⏳ Esperando arranque completo..."
        sleep 90
    fi
}

function verify_fix() {
    echo "🧪 Verificando que la corrección funcionó..."
    
    # Esperar un poco más
    sleep 30
    
    # Verificar estado de contenedores
    api_status=$(docker-compose ps api | grep -i healthy)
    if [ -n "$api_status" ]; then
        echo "✅ La API está funcionando correctamente"
        
        # Probar endpoint
        response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/productos/test 2>/dev/null)
        if [ "$response" = "200" ]; then
            echo "✅ La API responde correctamente"
            echo "🎉 PROBLEMA SOLUCIONADO!"
            return 0
        fi
    fi
    
    echo "❌ El problema persiste. Mostrando información de diagnóstico..."
    echo ""
    echo "Estado de contenedores:"
    docker-compose ps
    echo ""
    echo "Últimos logs de la API:"
    docker-compose logs --tail=30 api
    
    return 1
}

# Ejecutar solución automática
echo "🚀 Iniciando solución automática..."
check_logs_and_suggest
verify_fix

if [ $? -ne 0 ]; then
    echo ""
    echo "🆘 SOLUCIONES MANUALES:"
    echo "====================="
    echo "1. Revisar logs completos: docker-compose logs api"
    echo "2. Reinicio completo: docker-compose down && docker-compose up -d"
    echo "3. Rebuild completo: docker-compose build --no-cache && docker-compose up -d"
    echo "4. Si persiste, ejecutar: ./diagnostico.sh"
fi