# 🍰 Panadería API - Sistema Completo de Gestión

API REST completa para panadería con autenticación JWT, catálogo extenso, sistema de descuentos especiales y seguimiento de pedidos. Incluye soporte para pedidos anónimos y gestión de roles en tiempo real.

## 📑 Documentación

- **[API_DOCUMENTATION.md](API_DOCUMENTATION.md)** - Documentación completa de todos los endpoints

## 🔗 Enlaces de Producción

- **API Base**: http://168.197.50.14:8080
- **Test de Conectividad**: http://168.197.50.14:8080/api/productos/test
- **Catálogo de Productos**: http://168.197.50.14:8080/api/productos

## 🆕 Funcionalidades Implementadas

- ✅ **Pedidos Anónimos**: Soporte completo para pedidos sin registro de usuario
- ✅ **Gestión de Roles**: Cambio dinámico de roles (CLIENTE/ADMIN/EMPLEADO)
- ✅ **Dual Sales Flow**: Ventas directas + conversión pedido→venta
- ✅ **Autenticación JWT**: Sistema completo de tokens con refresh
- ✅ **Generación PDF**: Boletas y reportes automáticos

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
- **✨ Pedidos Anónimos**: Crear pedidos sin registro de usuario
- **✨ Pedidos con Usuario**: Acceso a descuentos y beneficios especiales
- Seguimiento completo desde preparación hasta entrega
- Generación automática de boletas
- Notificaciones de estado
- Selección de fechas de entrega
- Sistema inteligente de descuentos automáticos

### 🛠️ **DevOps y Despliegue**
- **Despliegue Preservando Datos**: Scripts que no eliminan información existente
- **Diagnóstico Automático**: Detección y solución inteligente de problemas
- **Healthcheck Robusto**: Monitoreo de salud de contenedores
- **Scripts de Solución**: Herramientas para resolver problemas comunes

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

## 📚 API Reference

> 📖 **Documentación Completa de Endpoints**: [API_DOCUMENTATION.md](API_DOCUMENTATION.md)

### **Endpoints Principales**

```bash
# Productos
GET /api/productos                           # Catálogo completo
GET /api/productos/test                      # Test de conectividad

# Usuarios  
POST /api/usuarios/registro                  # Registrar usuario
POST /api/usuarios/login                     # Iniciar sesión

# Pedidos
POST /api/pedidos                            # Crear pedido
GET /api/pedidos/{numeroPedido}/seguimiento  # Seguimiento

# Ventas
GET /api/ventas/estadisticas                 # Estadísticas
GET /api/ventas/hoy                          # Ventas del día
```

**Total de endpoints disponibles**: 47+ endpoints distribuidos en 4 controladores

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
- **Estado**: ✅ Operativo con todas las correcciones aplicadas
- **Base de datos**: MySQL configurada con soporte para pedidos anónimos
- **Última actualización**: 2025-11-14
- **Características**: ✅ Pedidos anónimos | ✅ Preservación de datos | ✅ Diagnóstico automático

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

### 🚀 Comandos de Despliegue Actualizados

#### **Despliegue Preservando Datos (Recomendado)**
```bash
# 1. Generar paquete de despliegue
.\deploy-to-vps.bat  # Windows
# o
./deploy-to-vps.sh   # Linux/Mac

# 2. Subir al VPS
scp panaderia-api-deploy.zip root@168.197.50.14:/root/

# 3. Desplegar preservando datos existentes
ssh root@168.197.50.14
cd /root && unzip -o panaderia-api-deploy.zip
./deploy.sh
```

#### **Solución Automática de Problemas**
```bash
# Si hay problemas después del despliegue
./solucion-rapida.sh      # Solución automática
./diagnostico.sh          # Diagnóstico completo
```

#### **Despliegue Limpio (Solo si es necesario)**
```bash
# ⚠️ BORRA TODOS LOS DATOS
./deploy-clean.sh
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
- ✅ **Gestión completa de pedidos** (con o sin usuario registrado)
- ✅ **Seguimiento de estados** de pedidos
- ✅ **Sistema de ventas** y estadísticas
- ✅ **Pedidos y ventas sin registro**: Los clientes no necesitan estar registrados para realizar compras

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