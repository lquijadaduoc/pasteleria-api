# 📚 Documentación Completa de la API - Panadería

Esta documentación contiene TODOS los endpoints implementados en la API de la panadería, organizados por controlador.

## 🏗️ Base URL
- **Desarrollo Local**: `http://localhost:8080`
- **Producción (VPS)**: `http://168.197.50.14:8080`

---

## 🍰 ProductController - `/api/productos`

### Consultas Básicas
```http
GET /api/productos
# Obtener todos los productos

GET /api/productos/categories  
# Obtener todas las categorías disponibles

GET /api/productos/{id}
# Obtener producto por ID

GET /api/productos/codigo/{codigo}
# Obtener producto por código (ej: TC001)

GET /api/productos/test
# Test de conectividad del controlador
```

### Filtros por Categoría y Características
```http
GET /api/productos/categoria/{categoria}
# Filtrar por categoría (TORTAS_CUADRADAS, TORTAS_CIRCULARES, etc.)

GET /api/productos/forma/{forma}
# Filtrar por forma (CIRCULAR, CUADRADA)

GET /api/productos/tamaño/{tamaño}
# Filtrar por tamaño (PEQUEÑO, MEDIANO, GRANDE)

GET /api/productos/personalizables
# Obtener solo productos personalizables

GET /api/productos/sin-azucar
# Obtener productos sin azúcar

GET /api/productos/sin-gluten
# Obtener productos sin gluten

GET /api/productos/veganos
# Obtener productos veganos
```

### Búsquedas Avanzadas
```http
GET /api/productos/buscar?q={termino}
# Búsqueda por nombre/descripción

GET /api/productos/busqueda-avanzada?categoria={cat}&personalizable={bool}&sinAzucar={bool}
# Búsqueda con múltiples filtros

GET /api/productos/precio?min={min}&max={max}
# Filtrar por rango de precios

GET /api/productos/recomendados
# Obtener productos recomendados
```

### Gestión de Productos (Admin)
```http
POST /api/productos
# Crear nuevo producto
Content-Type: application/json
{
  "codigo": "string",
  "nombre": "string", 
  "descripcion": "string",
  "precio": number,
  "categoria": "enum",
  "forma": "enum",
  "tamaño": "enum",
  "personalizable": boolean,
  "sinAzucar": boolean,
  "sinGluten": boolean,
  "vegano": boolean
}

PUT /api/productos/{id}
# Actualizar producto existente

DELETE /api/productos/{id}
# Eliminar producto
```

### Gestión de Inventario
```http
GET /api/productos/{id}/stock
# Consultar stock de un producto

PUT /api/productos/{id}/stock
# Actualizar stock
Content-Type: application/json
{ "stock": number }

POST /api/productos/{id}/stock
# Añadir stock
Content-Type: application/json
{ "cantidad": number }

GET /api/productos/inventario
# Reporte completo de inventario

GET /api/productos/mas-vendidos
# Productos más vendidos
```

---

## 👤 UserController - `/api/usuarios`

### Autenticación
```http
POST /api/usuarios/registro
# Registrar nuevo usuario
Content-Type: application/json
{
  "nombre": "string",
  "apellido": "string", 
  "email": "string",
  "password": "string",
  "edad": number,
  "esEstudianteDuoc": boolean
}

POST /api/usuarios/login
# Iniciar sesión
Content-Type: application/json
{
  "email": "string",
  "password": "string"
}
```

### Gestión de Descuentos
```http
POST /api/usuarios/{email}/codigo-descuento
# Aplicar código de descuento
Content-Type: application/json
{ "codigo": "string" }

GET /api/usuarios/{email}/descuentos
# Consultar descuentos activos del usuario
```

### Consultas de Usuarios
```http
GET /api/usuarios
# Obtener todos los usuarios (Admin)

GET /api/usuarios/email/{email}
# Buscar usuario por email

GET /api/usuarios/test
# Test de conectividad del controlador
```

---

## 📦 PedidoController - `/api/pedidos`

### Gestión de Pedidos
```http
POST /api/pedidos
# Crear nuevo pedido
Content-Type: application/json
{
  "usuarioEmail": "string",
  "fechaEntrega": "YYYY-MM-DD",
  "productos": [
    {
      "productId": number,
      "cantidad": number,
      "mensaje": "string"
    }
  ]
}

GET /api/pedidos
# Obtener todos los pedidos

GET /api/pedidos/{numeroPedido}/seguimiento
# Seguimiento de pedido por número

PUT /api/pedidos/{id}/estado
# Actualizar estado del pedido
Content-Type: application/json
{ "estado": "EN_PREPARACION|LISTO|ENTREGADO" }
```

### Consultas por Usuario
```http
GET /api/pedidos/usuario/{email}
# Pedidos de un usuario específico

GET /api/pedidos/cliente/{email}
# Pedidos como cliente (alias de usuario)
```

### Conversión a Ventas
```http
POST /api/pedidos/{id}/convertir-a-venta
# Convertir pedido completado en venta
```

### Test
```http
GET /api/pedidos/test
# Test de conectividad del controlador
```

---

## 💰 VentaController - `/api/ventas`

### Gestión de Ventas
```http
POST /api/ventas
# Registrar nueva venta
Content-Type: application/json
{
  "usuarioId": number,
  "productos": [
    {
      "productId": number,
      "cantidad": number,
      "precioUnitario": number
    }
  ],
  "metodoPago": "string",
  "descuentoAplicado": number
}

GET /api/ventas
# Obtener todas las ventas

GET /api/ventas/{id}
# Obtener venta por ID

PUT /api/ventas/{id}/cancelar
# Cancelar una venta
```

### Reportes y Estadísticas
```http
GET /api/ventas/hoy
# Ventas del día actual

GET /api/ventas/estadisticas
# Estadísticas generales de ventas

GET /api/ventas/reporte?inicio=YYYY-MM-DD&fin=YYYY-MM-DD
# Reporte de ventas por período

GET /api/ventas/resumen
# Resumen ejecutivo de ventas
```

### Test
```http
GET /api/ventas/test
# Test de conectividad del controlador
```

---

## 🔧 Códigos de Estado HTTP

| Código | Significado | Uso |
|--------|-------------|-----|
| 200 | OK | Operación exitosa |
| 201 | Created | Recurso creado exitosamente |
| 400 | Bad Request | Error en los datos enviados |
| 401 | Unauthorized | No autenticado |
| 403 | Forbidden | No autorizado para la operación |
| 404 | Not Found | Recurso no encontrado |
| 500 | Internal Server Error | Error interno del servidor |

---

## 📋 Enums y Valores Permitidos

### Categorías de Productos
- `TORTAS_CUADRADAS`
- `TORTAS_CIRCULARES` 
- `POSTRES_INDIVIDUALES`
- `PRODUCTOS_SIN_AZUCAR`
- `PASTELERIA_TRADICIONAL`
- `PRODUCTOS_SIN_GLUTEN`
- `PRODUCTOS_VEGANA`
- `TORTAS_ESPECIALES`

### Formas de Torta
- `CIRCULAR`
- `CUADRADA`

### Tamaños
- `PEQUEÑO`
- `MEDIANO`
- `GRANDE`

### Estados de Pedido
- `PENDIENTE`
- `EN_PREPARACION`
- `LISTO`
- `ENTREGADO`
- `CANCELADO`

---

## 🧪 Ejemplos de Uso

### Crear Usuario y Hacer Pedido
```bash
# 1. Registrar usuario
curl -X POST http://168.197.50.14:8080/api/usuarios/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Pérez", 
    "email": "juan@test.com",
    "password": "123456",
    "edad": 25,
    "esEstudianteDuoc": false
  }'

# 2. Ver productos disponibles
curl http://168.197.50.14:8080/api/productos

# 3. Crear pedido
curl -X POST http://168.197.50.14:8080/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioEmail": "juan@test.com",
    "fechaEntrega": "2025-11-20",
    "productos": [
      {
        "productId": 1,
        "cantidad": 1,
        "mensaje": "Feliz Cumpleaños María"
      }
    ]
  }'
```

### Consultar Estadísticas
```bash
# Ver ventas del día
curl http://168.197.50.14:8080/api/ventas/hoy

# Ver productos más vendidos
curl http://168.197.50.14:8080/api/productos/mas-vendidos

# Estadísticas generales
curl http://168.197.50.14:8080/api/ventas/estadisticas
```

---

**📅 Última actualización**: 14 de noviembre de 2025  
**🔗 Repositorio**: https://github.com/lquijadaduoc/pasteleria-api  
**🌐 API en Producción**: http://168.197.50.14:8080