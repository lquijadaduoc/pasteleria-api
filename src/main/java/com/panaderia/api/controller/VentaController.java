package com.panaderia.api.controller;

import com.panaderia.api.entity.Venta;
import com.panaderia.api.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador REST para manejar las ventas
 * Incluye endpoints para crear, consultar y gestionar ventas
 */
@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "*")
public class VentaController {
    
    @Autowired
    private VentaService ventaService;
    
    /**
     * Registrar una nueva venta
     * POST /api/ventas
     */
    @PostMapping
    public ResponseEntity<?> registrarVenta(@RequestBody Map<String, Object> datosVenta) {
        try {
            Venta venta = ventaService.procesarVenta(datosVenta);
            
            Map<String, Object> ventaData = new HashMap<>();
            ventaData.put("id", venta.getId());
            ventaData.put("numeroVenta", venta.getNumeroVenta());
            ventaData.put("fechaVenta", venta.getFechaVenta());
            ventaData.put("nombreCliente", venta.getNombreCliente());
            ventaData.put("emailCliente", venta.getEmailCliente());
            ventaData.put("subtotal", venta.getSubtotal());
            ventaData.put("descuento", venta.getDescuento());
            ventaData.put("total", venta.getTotal());
            ventaData.put("metodoPago", venta.getMetodoPago());
            ventaData.put("estado", venta.getEstado());
            ventaData.put("totalItems", venta.getTotalItems());
            ventaData.put("totalProductos", venta.getTotalProductos());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Venta registrada exitosamente",
                "venta", ventaData
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", "Error al registrar venta: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Obtener todas las ventas
     * GET /api/ventas
     */
    @GetMapping
    public ResponseEntity<?> listarVentas() {
        try {
            List<Venta> ventas = ventaService.obtenerTodasLasVentas();
            
            List<Map<String, Object>> ventasSummary = ventas.stream()
                .map(venta -> {
                    Map<String, Object> ventaMap = new HashMap<>();
                    ventaMap.put("id", venta.getId());
                    ventaMap.put("numeroVenta", venta.getNumeroVenta());
                    ventaMap.put("fechaVenta", venta.getFechaVenta());
                    ventaMap.put("nombreCliente", venta.getNombreCliente() != null ? venta.getNombreCliente() : "Cliente Anónimo");
                    ventaMap.put("emailCliente", venta.getEmailCliente() != null ? venta.getEmailCliente() : "N/A");
                    ventaMap.put("total", venta.getTotal());
                    ventaMap.put("metodoPago", venta.getMetodoPago());
                    ventaMap.put("estado", venta.getEstado());
                    ventaMap.put("totalItems", venta.getTotalItems());
                    return ventaMap;
                })
                .toList();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "count", ventas.size(),
                "ventas", ventasSummary
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al obtener ventas: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Obtener detalle de una venta específica
     * GET /api/ventas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerVenta(@PathVariable Long id) {
        try {
            Optional<Venta> ventaOpt = ventaService.obtenerVentaPorId(id);
            
            if (!ventaOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Venta no encontrada"
                ));
            }
            
            Venta venta = ventaOpt.get();
            
            List<Map<String, Object>> itemsList = venta.getItems().stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("producto", item.getProduct().getNombre());
                itemMap.put("cantidad", item.getCantidad());
                itemMap.put("precioUnitario", item.getPrecioUnitario());
                itemMap.put("subtotal", item.getSubtotal());
                itemMap.put("mensajePersonalizado", item.getMensajePersonalizado());
                return itemMap;
            }).toList();
            
            Map<String, Object> ventaDetalle = new HashMap<>();
            ventaDetalle.put("id", venta.getId());
            ventaDetalle.put("numeroVenta", venta.getNumeroVenta());
            ventaDetalle.put("fechaVenta", venta.getFechaVenta());
            ventaDetalle.put("nombreCliente", venta.getNombreCliente());
            ventaDetalle.put("emailCliente", venta.getEmailCliente());
            ventaDetalle.put("subtotal", venta.getSubtotal());
            ventaDetalle.put("descuento", venta.getDescuento());
            ventaDetalle.put("total", venta.getTotal());
            ventaDetalle.put("metodoPago", venta.getMetodoPago());
            ventaDetalle.put("estado", venta.getEstado());
            ventaDetalle.put("observaciones", venta.getObservaciones());
            ventaDetalle.put("items", itemsList);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "venta", ventaDetalle
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al obtener venta: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Obtener ventas del día
     * GET /api/ventas/hoy
     */
    @GetMapping("/hoy")
    public ResponseEntity<?> obtenerVentasDelDia() {
        try {
            List<Venta> ventas = ventaService.obtenerVentasDelDia();
            
            List<Map<String, Object>> ventasSummary = ventas.stream()
                .map(venta -> {
                    Map<String, Object> ventaMap = new HashMap<>();
                    ventaMap.put("id", venta.getId());
                    ventaMap.put("numeroVenta", venta.getNumeroVenta());
                    ventaMap.put("fechaVenta", venta.getFechaVenta());
                    ventaMap.put("nombreCliente", venta.getNombreCliente() != null ? venta.getNombreCliente() : "Cliente Anónimo");
                    ventaMap.put("total", venta.getTotal());
                    ventaMap.put("estado", venta.getEstado());
                    return ventaMap;
                })
                .toList();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "fecha", java.time.LocalDate.now(),
                "count", ventas.size(),
                "ventas", ventasSummary
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al obtener ventas del día: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Obtener estadísticas de ventas
     * GET /api/ventas/estadisticas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        try {
            Map<String, Object> estadisticas = ventaService.obtenerEstadisticasVentas();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "estadisticas", estadisticas
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al obtener estadísticas: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Cancelar una venta
     * PUT /api/ventas/{id}/cancelar
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelarVenta(@PathVariable Long id, @RequestBody Map<String, String> datos) {
        try {
            String motivo = datos.get("motivo");
            Venta venta = ventaService.cancelarVenta(id, motivo);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Venta cancelada exitosamente",
                "venta", Map.of(
                    "id", venta.getId(),
                    "numeroVenta", venta.getNumeroVenta(),
                    "estado", venta.getEstado(),
                    "observaciones", venta.getObservaciones()
                )
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", "Error al cancelar venta: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Reporte de ventas por rango de fechas
     * GET /api/ventas/reporte?fechaInicio=2025-11-01&fechaFin=2025-11-30
     */
    @GetMapping("/reporte")
    public ResponseEntity<?> reporteVentas(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {
        try {
            List<Venta> todasLasVentas = ventaService.obtenerTodasLasVentas();
            
            // Si no se especifican fechas, usar el mes actual
            if (fechaInicio == null || fechaFin == null) {
                java.time.LocalDate ahora = java.time.LocalDate.now();
                fechaInicio = ahora.withDayOfMonth(1).toString();
                fechaFin = ahora.withDayOfMonth(ahora.lengthOfMonth()).toString();
            }
            
            // Filtrar por rango de fechas
            java.time.LocalDate inicio = java.time.LocalDate.parse(fechaInicio);
            java.time.LocalDate fin = java.time.LocalDate.parse(fechaFin);
            
            List<Venta> ventasFiltradas = todasLasVentas.stream()
                .filter(v -> {
                    java.time.LocalDate fechaVenta = v.getFechaVenta().toLocalDate();
                    return !fechaVenta.isBefore(inicio) && !fechaVenta.isAfter(fin);
                })
                .toList();
            
            // Calcular métricas del reporte
            java.math.BigDecimal totalIngresos = ventasFiltradas.stream()
                .filter(v -> v.getEstado() == Venta.EstadoVenta.COMPLETADA)
                .map(Venta::getTotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            
            int totalVentas = (int) ventasFiltradas.stream()
                .filter(v -> v.getEstado() == Venta.EstadoVenta.COMPLETADA)
                .count();
            
            int totalCanceladas = (int) ventasFiltradas.stream()
                .filter(v -> v.getEstado() == Venta.EstadoVenta.CANCELADA)
                .count();
            
            // Calcular promedio por venta
            java.math.BigDecimal promedioVenta = totalVentas > 0 ? 
                totalIngresos.divide(java.math.BigDecimal.valueOf(totalVentas), 2, java.math.RoundingMode.HALF_UP) : 
                java.math.BigDecimal.ZERO;
            
            Map<String, Object> reporte = new HashMap<>();
            reporte.put("periodo", Map.of("inicio", fechaInicio, "fin", fechaFin));
            reporte.put("totalVentas", totalVentas);
            reporte.put("totalCanceladas", totalCanceladas);
            reporte.put("totalIngresos", totalIngresos);
            reporte.put("promedioVenta", promedioVenta);
            reporte.put("ventasDetalle", ventasFiltradas.stream().map(venta -> {
                Map<String, Object> detalle = new HashMap<>();
                detalle.put("id", venta.getId());
                detalle.put("numeroVenta", venta.getNumeroVenta());
                detalle.put("fecha", venta.getFechaVenta().toLocalDate());
                detalle.put("cliente", venta.getNombreCliente());
                detalle.put("total", venta.getTotal());
                detalle.put("estado", venta.getEstado());
                detalle.put("metodoPago", venta.getMetodoPago());
                return detalle;
            }).toList());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "reporte", reporte
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al generar reporte: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Resumen ejecutivo de ventas
     * GET /api/ventas/resumen
     */
    @GetMapping("/resumen")
    public ResponseEntity<?> resumenEjecutivo() {
        try {
            Map<String, Object> estadisticas = ventaService.obtenerEstadisticasVentas();
            List<Venta> todasLasVentas = ventaService.obtenerTodasLasVentas();
            
            // Calcular métricas adicionales
            java.time.LocalDate hoy = java.time.LocalDate.now();
            java.time.LocalDate ayer = hoy.minusDays(1);
            
            List<Venta> ventasHoy = todasLasVentas.stream()
                .filter(v -> v.getFechaVenta().toLocalDate().equals(hoy))
                .filter(v -> v.getEstado() == Venta.EstadoVenta.COMPLETADA)
                .toList();
            
            List<Venta> ventasAyer = todasLasVentas.stream()
                .filter(v -> v.getFechaVenta().toLocalDate().equals(ayer))
                .filter(v -> v.getEstado() == Venta.EstadoVenta.COMPLETADA)
                .toList();
            
            java.math.BigDecimal ingresoHoy = ventasHoy.stream()
                .map(Venta::getTotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            
            java.math.BigDecimal ingresoAyer = ventasAyer.stream()
                .map(Venta::getTotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            
            // Calcular crecimiento
            String tendencia = "Sin datos de ayer";
            if (ingresoAyer.compareTo(java.math.BigDecimal.ZERO) > 0) {
                java.math.BigDecimal crecimiento = ingresoHoy.subtract(ingresoAyer)
                    .divide(ingresoAyer, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(java.math.BigDecimal.valueOf(100));
                tendencia = crecimiento.compareTo(java.math.BigDecimal.ZERO) >= 0 ? 
                    "+" + crecimiento + "%" : crecimiento + "%";
            }
            
            Map<String, Object> resumen = new HashMap<>();
            resumen.put("fecha", hoy);
            resumen.put("ventasHoy", ventasHoy.size());
            resumen.put("ingresoHoy", ingresoHoy);
            resumen.put("ventasAyer", ventasAyer.size());
            resumen.put("ingresoAyer", ingresoAyer);
            resumen.put("tendencia", tendencia);
            resumen.put("estadisticasGenerales", estadisticas);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "resumen", resumen
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error al generar resumen: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Endpoint de prueba
     * GET /api/ventas/test
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("💰 API de Ventas funcionando correctamente!");
    }
}