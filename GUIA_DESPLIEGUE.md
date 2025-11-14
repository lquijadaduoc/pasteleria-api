# Guía de Despliegue - Preservando Datos

## 🎯 Problema Resuelto

El error `user_id cannot be null` se solucionó creando un script SQL completo que:

1. ✅ Crea todas las tablas desde cero con la configuración correcta
2. ✅ Permite `user_id = NULL` en la tabla pedidos (para pedidos anónimos) 
3. ✅ Preserva los datos existentes entre redespliegues
4. ✅ Solo inserta datos iniciales si las tablas están vacías

## 🚀 Opciones de Despliegue

### Opción 1: Actualización Preservando Datos (Recomendada)

**Para actualizaciones de código SIN borrar datos:**

```bash
# 1. Subir el paquete actualizado
scp panaderia-api-deploy.zip root@168.197.50.14:/root/

# 2. En el VPS
ssh root@168.197.50.14
cd /root
unzip -o panaderia-api-deploy.zip
./deploy.sh
```

**Qué hace:**
- ✅ Detiene contenedores con `docker-compose stop` (NO `down`)
- ✅ Preserva volúmenes y datos de la base de datos  
- ✅ Solo reconstruye la aplicación
- ✅ Mantiene: usuarios, pedidos, productos, configuraciones

### Opción 2: Despliegue Limpio (Solo si es necesario)

**Para empezar desde cero (BORRA TODOS LOS DATOS):**

```bash
# En el VPS
ssh root@168.197.50.14
cd /root
./deploy-clean.sh
```

**⚠️ Advertencia:** Este script elimina TODO y requiere confirmación manual.

## 🔧 Correcciones Implementadas

### 1. Script SQL Actualizado (`sql/init.sql`)
- Crea todas las tablas desde cero
- `user_id BIGINT NULL` en tabla pedidos
- Foreign keys que permiten NULL
- Inserta datos solo si las tablas están vacías

### 2. Docker Compose Actualizado  
- Monta `./sql:/docker-entrypoint-initdb.d`
- El script se ejecuta solo al crear el contenedor por primera vez

### 3. Script de Despliegue Mejorado
- `docker-compose stop` en lugar de `down`
- NO elimina volúmenes de datos
- Preserva información entre actualizaciones

## 🧪 Verificar la Solución

Después del despliegue, probar pedido anónimo:

```bash
curl -X POST http://168.197.50.14:8080/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "emailUsuario": "test@example.com",
    "observaciones": "Pedido sin usuario registrado",
    "items": [{"productId": 1, "cantidad": 1}]
  }'
```

## 📊 Estado Después de la Corrección

| Escenario | Resultado |
|-----------|-----------|
| Pedido con usuario registrado | ✅ Funciona normal |
| Pedido sin usuario (anónimo) | ✅ Funciona con user_id=NULL |
| Actualizar código | ✅ Preserva todos los datos |
| Primera instalación | ✅ Crea estructura + datos iniciales |
| Redespliegue | ✅ Solo actualiza app, mantiene datos |

## 🎁 Bonus: Datos Iniciales Incluidos

- 10 productos de ejemplo (tortas, panes, kuchens, etc.)
- Usuario admin: `admin@panaderia.com`
- Todas las tablas con índices optimizados
- Configuración de timezone Chile

**La aplicación estará lista para usar inmediatamente después del primer despliegue.**