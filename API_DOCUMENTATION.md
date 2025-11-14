# 📚 Documentación Completa de la API - Panadería

Esta documentación contiene TODOS los endpoints implementados en la API de la panadería, organizados por controlador.

**🆕 Última actualización:** Noviembre 14, 2025 - Incluye soporte para pedidos anónimos y correcciones de estabilidad.

## 🏗️ Base URL
- **Desarrollo Local**: `http://localhost:8080`
- **Producción (VPS)**: `http://168.197.50.14:8080` ✅ **Operativo**

## 🎯 Características Destacadas

- ✅ **Pedidos Anónimos**: Crear pedidos sin necesidad de registro
- ✅ **Descuentos Automáticos**: Para usuarios registrados (50+ años, código FELICES50)
- ✅ **Tortas Gratis**: Para estudiantes Duoc (@duoc.cl) en cumpleaños
- ✅ **Catálogo Completo**: 16+ productos con personalización
- ✅ **Sistema Robusto**: Con diagnóstico automático y preservación de datos

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

> ℹ️ **Importante**: Los pedidos NO requieren que el usuario esté registrado en el sistema.
> - Si el email corresponde a un usuario registrado, se aplicarán descuentos y beneficios automáticamente
> - Si el email NO está registrado, el pedido se creará igualmente sin descuentos especiales

```http
POST /api/pedidos
# Crear nuevo pedido (usuario registrado o no)
Content-Type: application/json
{
  "emailUsuario": "string",          # Email del cliente (registrado o no)
  "fechaEntrega": "2025-11-20T10:00:00",  # Formato ISO DateTime (opcional)
  "observaciones": "string",         # Notas adicionales (opcional)
  "items": [
    {
      "productId": number,           # ID del producto
      "cantidad": number,            # Cantidad solicitada
      "mensajePersonalizado": "string"  # Mensaje para tortas (opcional)
    }
  ]
}

# Respuesta exitosa:
{
  "success": true,
  "message": "Pedido creado exitosamente",
  "pedido": {
    "id": number,
    "numeroPedido": "PAN-1234567890",
    "estado": "RECIBIDO",
    "subtotal": number,
    "descuento": number,           # 0 si no es usuario registrado
    "total": number,
    "fechaCreacion": "2025-11-14T...",
    "fechaEntrega": "2025-11-20T..."
  }
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

### 🌟 Ejemplos de Pedidos

#### **Pedido Anónimo (Sin Usuario Registrado)**
```bash
curl -X POST http://168.197.50.14:8080/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "emailUsuario": "cliente@gmail.com",
    "observaciones": "Pedido para cumpleaños",
    "items": [
      {
        "productId": 1,
        "cantidad": 1,
        "mensajePersonalizado": "Feliz Cumpleaños María"
      }
    ]
  }'

# Resultado: Pedido sin descuentos, user_id = null en BD
```

#### **Pedido de Usuario Registrado con Descuentos**
```bash
curl -X POST http://168.197.50.14:8080/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "emailUsuario": "juan@duoc.cl",  # Usuario registrado
    "fechaEntrega": "2025-11-20T15:00:00",
    "items": [
      {
        "productId": 1,
        "cantidad": 2,
        "mensajePersonalizado": "Tortas para la oficina"
      }
    ]
  }'

# Resultado: Con descuentos automáticos aplicados
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

> ℹ️ **Importante**: Las ventas NO requieren que el usuario esté registrado en el sistema.
> - Si el email corresponde a un usuario registrado, se aplicarán descuentos automáticamente
> - Si el email NO está registrado, la venta se procesará sin descuentos especiales

```http
POST /api/ventas
# Registrar nueva venta (usuario registrado o no)
Content-Type: application/json
{
  "emailCliente": "string",          # Email del cliente (opcional)
  "nombreCliente": "string",         # Nombre del cliente
  "metodoPago": "EFECTIVO|TARJETA_CREDITO|TARJETA_DEBITO|TRANSFERENCIA",
  "observaciones": "string",         # Notas adicionales (opcional)
  "items": [
    {
      "productId": number,           # ID del producto
      "cantidad": number,            # Cantidad vendida
      "mensajePersonalizado": "string"  # Mensaje personalizado (opcional)
    }
  ]
}

# Respuesta exitosa:
{
  "success": true,
  "message": "Venta registrada exitosamente",
  "venta": {
    "id": number,
    "numeroVenta": "V20251114...",
    "fechaVenta": "2025-11-14T...",
    "nombreCliente": "string",
    "emailCliente": "string",
    "subtotal": number,
    "descuento": number,             # 0 si no es usuario registrado
    "total": number,
    "metodoPago": "string",
    "estado": "COMPLETADA"
  }
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