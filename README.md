# 🍰 Panadería API - Sistema Completo de Gestión

API REST completa para panadería con autenticación, catálogo extenso, sistema de descuentos especiales y seguimiento de pedidos.

## ✨ Características Principales

### � **Sistema de Usuarios y Descuentos**
- **Descuento 50%** para usuarios mayores de 50 años
- **Descuento 10%** de por vida con código "FELICES50"
- **Tortas gratis** para estudiantes Duoc en su cumpleaños (@duoc.cl)

### 🛍️ **Catálogo Completo**
- **Tortas Cuadradas y Circulares** con diferentes tamaños
- **Postres Individuales** (Mousse, Tiramisú, etc.)
- **Productos Sin Azúcar, Sin Gluten y Veganos**
- **Personalización** con mensajes especiales
- **16 productos predefinidos** según especificaciones

### 📦 **Gestión de Pedidos**
- Seguimiento completo desde preparación hasta entrega
- Generación automática de boletas
- Notificaciones de estado
- Selección de fechas de entrega

## 🚀 Inicio Rápido

### Prerrequisitos
- **Docker Desktop** instalado y ejecutándose
- Java 17+ (opcional para desarrollo local)

### 1. Testing con Docker Desktop

#### Para Windows:
```powershell
# Ejecutar el script de testing
.\test-docker.bat
```

#### Manualmente:
```bash
# 1. Levantar servicios
docker-compose up -d --build

# 2. Probar endpoints
curl http://localhost:8080/api/productos/test
curl http://localhost:8080/api/productos
```

### 2. Configuración Manual

```bash
# 1. Crear archivo de configuración
cp .env.example .env
# Edita .env con tus configuraciones

# 2. Construir la aplicación (si no tienes el JAR)
./mvnw clean package -DskipTests

# 3. Levantar servicios con Docker
docker-compose up --build -d

# 4. Ver logs
docker-compose logs -f panaderia-api
```

### 3. Verificar Funcionamiento

- **API**: http://tu-ip:8080/api/productos/test
- **Catálogo**: http://tu-ip:8080/api/productos
- **Adminer**: http://tu-ip:8081 (MySQL Web Interface)

## 📚 Catálogo de Productos

### Categorías Disponibles
1. **TORTAS_CUADRADAS** - TC001, TC002
2. **TORTAS_CIRCULARES** - TT001, TT002
3. **POSTRES_INDIVIDUALES** - PI001, PI002
4. **PRODUCTOS_SIN_AZUCAR** - PSA001, PSA002
5. **PASTELERIA_TRADICIONAL** - PT001, PT002
6. **PRODUCTOS_SIN_GLUTEN** - PG001, PG002
7. **PRODUCTOS_VEGANA** - PV001, PV002
8. **TORTAS_ESPECIALES** - TE001, TE002

### Ejemplos de Productos

| Código | Producto | Precio | Personalizable |
|--------|----------|--------|----------------|
| TC001 | Torta Cuadrada de Chocolate | $45.000 | ✅ |
| TT001 | Torta Circular de Vainilla | $40.000 | ✅ |
| PI001 | Mousse de Chocolate | $5.000 | ❌ |
| TE001 | Torta Especial de Cumpleaños | $55.000 | ✅ |

## 📚 Endpoints del API

### **Productos y Catálogo**

```bash
# Obtener todos los productos
GET /api/productos

# Obtener producto por ID
GET /api/productos/{id}

# Test de conectividad
GET /api/productos/test
```

### **Sistema de Usuarios**

```bash
# Registro de usuario
POST /api/usuarios/registro
# Body: { "nombre": "string", "apellido": "string", "email": "string", "password": "string", "edad": number, "esEstudianteDuoc": boolean }

# Login de usuario
POST /api/usuarios/login
# Body: { "email": "string", "password": "string" }

# Obtener usuario por email
GET /api/usuarios/email/{email}

# Obtener todos los usuarios
GET /api/usuarios

# Aplicar código de descuento
POST /api/usuarios/{id}/aplicar-codigo
# Body: { "codigo": "string" }
```

### **Pedidos y Seguimiento**

```bash
# Crear nuevo pedido
POST /api/pedidos
# Body: { "usuarioId": number, "fechaEntrega": "YYYY-MM-DD", "productos": [{"productId": number, "cantidad": number, "mensaje": "string"}] }

# Obtener todos los pedidos
GET /api/pedidos

# Obtener pedido por ID
GET /api/pedidos/{id}

# Obtener pedidos por usuario
GET /api/pedidos/usuario/{usuarioId}

# Actualizar estado del pedido
PUT /api/pedidos/{id}/estado
# Body: { "estado": "EN_PREPARACION|LISTO|ENTREGADO" }
```

### **Ventas y Estadísticas**

```bash
# Obtener todas las ventas
GET /api/ventas

# Obtener venta por ID
GET /api/ventas/{id}

# Obtener ventas por usuario
GET /api/ventas/usuario/{usuarioId}

# Obtener estadísticas de ventas
GET /api/ventas/estadisticas

# Obtener ventas por fecha
GET /api/ventas/fecha?inicio=YYYY-MM-DD&fin=YYYY-MM-DD
```

## �️ Configuración de Base de Datos

La aplicación se conecta automáticamente a MySQL y crea:
- **16 productos** del catálogo especificado
- **Tablas de usuarios** con sistema de descuentos
- **Sistema de pedidos** y seguimiento
- **Gestión de boletas** y notificaciones

### Credenciales MySQL
- **Host**: localhost:3307
- **Database**: panaderia_db
- **Usuario**: panaderia_user
- **Password**: panaderia_pass

## 📊 Interface Web MySQL (Adminer)
- **URL**: http://localhost:8081
- **Server**: mysql-db
- **Username**: panaderia_user
- **Password**: panaderia_pass

## 🎨 Especificaciones de Diseño

### Colores
- **Fondo Principal**: #FFF5E1 (Crema Pastel)
- **Acentos**: #FFC0CB (Rosa Suave), #8B4513 (Chocolate)
- **Texto**: #5D4037 (Marrón Oscuro), #B0BEC5 (Gris Claro)

### Tipografías
- **Principal**: Lato (sans-serif)
- **Encabezados**: Pacifico (cursiva artística)

## 🧪 Testing

### Probar API con PowerShell

```powershell
# Verificar que funciona
Invoke-RestMethod -Uri "http://localhost:8080/api/productos/test"

# Obtener catálogo completo
Invoke-RestMethod -Uri "http://localhost:8080/api/productos" | ConvertTo-Json

# Buscar tortas circulares
Invoke-RestMethod -Uri "http://localhost:8080/api/productos/forma/CIRCULAR" | ConvertTo-Json
```

## 🚀 Despliegue en VPS

### ✅ Estado Actual del Despliegue

**La API está actualmente desplegada y funcionando:**
- **URL de la API**: http://168.197.50.14:8080
- **Estado**: ✅ Operativo
- **Base de datos**: MySQL configurada y corriendo
- **Última actualización**: 2025-11-11

### Endpoints en Producción

```bash
# Verificar que la API está funcionando
curl http://168.197.50.14:8080/api/productos/test

# Obtener catálogo completo
curl http://168.197.50.14:8080/api/productos

# Registrar usuario
curl -X POST http://168.197.50.14:8080/api/usuarios/registro \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Juan","apellido":"Pérez","email":"juan@test.com","password":"123456","edad":25,"esEstudianteDuoc":false}'
```

### Configuración de Variables de Entorno

Para nuevos despliegues, edita el archivo `.env`:

```bash
# Variables para VPS
MYSQL_ROOT_PASSWORD=tu_password_super_seguro
MYSQL_PASSWORD=tu_password_usuario_seguro
API_PORT=8080
ALLOWED_ORIGINS=https://tu-dominio.com,https://tu-frontend.com
ADMINER_PORT=8081
```

### Comandos de Despliegue

```bash
# 1. Subir archivos al VPS
scp -r . usuario@168.197.50.14:/ruta/panaderia-api/

# 2. Conectar al VPS
ssh usuario@168.197.50.14

# 3. Navegar al directorio
cd /ruta/panaderia-api/

# 4. Construir y desplegar
docker-compose up --build -d
```

### Configuración de Firewall

```bash
# Puertos abiertos en el VPS
sudo ufw allow 8080/tcp  # API ✅ Configurado
sudo ufw allow 8081/tcp  # Adminer (opcional)
sudo ufw allow 3307/tcp  # MySQL (opcional)
```

## 🏗️ Arquitectura del Sistema

### **Tecnologías Implementadas**
- **Backend**: Spring Boot 3.x con Java 17
- **Base de Datos**: MySQL 8.0
- **ORM**: Spring Data JPA / Hibernate
- **Seguridad**: Spring Security (configuración personalizada)
- **Contenedores**: Docker & Docker Compose
- **Frontend**: React.js con Firebase Authentication

### **Controladores Implementados**
- ✅ **ProductController**: Gestión de productos y catálogo
- ✅ **UserController**: Registro, login y gestión de usuarios
- ✅ **PedidoController**: Creación y seguimiento de pedidos
- ✅ **VentaController**: Registro de ventas y estadísticas

### **Servicios Implementados**
- ✅ **AuthService**: Autenticación y registro de usuarios
- ✅ **DataInitializationService**: Población inicial de datos
- ✅ **VentaService**: Lógica de negocio para ventas

### **Características Funcionales**
- ✅ **Sistema de descuentos** automático por edad
- ✅ **Descuento especial** para estudiantes Duoc UC
- ✅ **Código promocional** "FELICES50"
- ✅ **Gestión completa de pedidos**
- ✅ **Seguimiento de estados** de pedidos
- ✅ **Sistema de ventas** y estadísticas

## 📋 Futuras Mejoras

- [ ] **Autenticación JWT** más robusta
- [ ] **Notificaciones por email**
- [ ] **Generación de boletas PDF**
- [ ] **Panel de administración web**
- [ ] **API de pagos** integrada

## 🔧 Comandos Útiles

```powershell
# Ver logs en tiempo real
docker-compose logs -f panaderia-api

# Reiniciar solo la aplicación
docker-compose restart panaderia-api

# Limpiar todo y empezar de nuevo
docker-compose down -v
docker-compose up --build -d

# Entrar al contenedor de la aplicación
docker exec -it panaderia-app bash

# Backup de base de datos
docker exec panaderia-mysql mysqldump -u panaderia_user -ppanaderia_pass panaderia_db > backup.sql
```

---

**¡Tu sistema de panadería está listo!** 🍰✨