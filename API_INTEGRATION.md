# API Integration - API de PASTELERIA 1000 SABORES

Guia practica para consumir la API desde frontends o servicios externos. Complementa a `API_DOCUMENTATION.md` (referencia de endpoints).

**Desarrolladores**: Luis Quijada Muñoz - David Santibañez Roca

## Entornos y headers
- Base local: `http://localhost:8080`
- Produccion: `http://168.197.50.14:8080`
- Headers recomendados: `Content-Type: application/json` y `Accept: application/json`.
- CORS: abierto para todos los origenes; no se requieren tokens ni API keys. El endpoint de login valida credenciales pero **no** emite token (todas las rutas estan abiertas).

## Forma de las respuestas
- Exitos: `200/201` con `success: true` y datos en objetos como `producto`, `pedido`, `venta`, etc.
- Errores: `4xx/5xx` con `success: false` y `message` describiendo el problema. Maneja `400` para datos invalidos, `401/403` si agregas seguridad futura, `404` cuando no existe el recurso.

## Flujos recomendados
### 1) Obtener catalogo y filtrar
- Lista completa: `GET /api/productos`
- Por categoria: `GET /api/productos/categoria/{categoria}`
- Busqueda: `GET /api/productos/buscar?q=texto`
- Rango de precio: `GET /api/productos/precio?min=10000&max=40000`

### 2) Registrar/consultar usuario (opcional)
- Registro: `POST /api/usuarios/registro` con `nombre`, `apellido`, `email`, `password`, `edad`, `esEstudianteDuoc`.
- Login: `POST /api/usuarios/login` (sin token de vuelta; se usa para validar credenciales).
- Consultar descuentos: `GET /api/usuarios/{email}/descuentos`
- Aplicar codigo: `POST /api/usuarios/{email}/codigo-descuento` con `{ "codigo": "FELICES50" }`

### 3) Crear pedido (usuario opcional)
`POST /api/pedidos`
```json
{
  "emailUsuario": "cliente@test.com",
  "fechaEntrega": "2025-11-20T10:00:00",   // ISO LocalDateTime, opcional
  "observaciones": "Incluir velas",
  "items": [
    { "productId": 1, "cantidad": 1, "mensajePersonalizado": "Feliz cumple" }
  ]
}
```
Respuesta clave: `numeroPedido`, `estado`, `subtotal`, `descuento`, `total`.

- Seguimiento: `GET /api/pedidos/{numeroPedido}/seguimiento`
- Cambiar estado: `PUT /api/pedidos/{id}/estado` con `{ "estado": "EN_PREPARACION|LISTO|ENTREGADO" }`
- Pedidos por email (registrado o no): `GET /api/pedidos/usuario/{email}`
- Convertir a venta: `POST /api/pedidos/{id}/convertir-a-venta` con `{ "metodoPago": "EFECTIVO|TARJETA_CREDITO|TARJETA_DEBITO|TRANSFERENCIA" }`

### 4) Venta directa (sin pedido)
`POST /api/ventas`
```json
{
  "emailCliente": "cliente@test.com",
  "nombreCliente": "Cliente",
  "metodoPago": "EFECTIVO",
  "observaciones": "Entrega inmediata",
  "items": [
    { "productId": 2, "cantidad": 2, "mensajePersonalizado": "Gracias" }
  ]
}
```
Respuesta: incluye `numeroVenta`, `total`, `estado`.

- Listado: `GET /api/ventas`
- Detalle: `GET /api/ventas/{id}`
- Cancelar: `PUT /api/ventas/{id}/cancelar` con `{ "motivo": "texto" }`
- Estadisticas/Reportes: `GET /api/ventas/estadisticas`, `GET /api/ventas/hoy`, `GET /api/ventas/reporte?fechaInicio=YYYY-MM-DD&fechaFin=YYYY-MM-DD`, `GET /api/ventas/resumen`

## Payloads y validaciones clave
- `productId` debe existir; si no, el item se omite en pedidos.
- `cantidad` debe ser > 0.
- `fechaEntrega` usa formato `YYYY-MM-DDTHH:MM:SS`.
- En productos, enums: `categoria` (`TORTAS_CUADRADAS`, `TORTAS_CIRCULARES`, etc.), `formaTorta` (`CIRCULAR`, `CUADRADA`), `tamaño` (`PEQUEÑO`, `MEDIANO`, `GRANDE`).
- En ventas, `metodoPago` acepta: `EFECTIVO`, `TARJETA_CREDITO`, `TARJETA_DEBITO`, `TRANSFERENCIA`.

## Ejemplos rapidos (curl)
- Catalogo: `curl http://localhost:8080/api/productos`
- Pedido rapido: 
```bash
curl -X POST http://localhost:8080/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{"emailUsuario":"demo@test.com","items":[{"productId":1,"cantidad":1}]}'
```
- Seguimiento: `curl http://localhost:8080/api/pedidos/PAN-<numero>/seguimiento`
- Venta directa:
```bash
curl -X POST http://localhost:8080/api/ventas \
  -H "Content-Type: application/json" \
  -d '{"nombreCliente":"Cliente Demo","metodoPago":"EFECTIVO","items":[{"productId":1,"cantidad":1}]}'
```
