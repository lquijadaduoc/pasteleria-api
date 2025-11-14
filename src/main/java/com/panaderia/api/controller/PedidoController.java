package com.panaderia.api.controller;

import com.panaderia.api.entity.Pedido;
import com.panaderia.api.entity.PedidoItem;
import com.panaderia.api.entity.Product;
import com.panaderia.api.entity.User;
import com.panaderia.api.entity.Venta;
import com.panaderia.api.repository.PedidoRepository;
import com.panaderia.api.repository.ProductRepository;
import com.panaderia.api.repository.UserRepository;
import com.panaderia.api.service.AuthService;
import com.panaderia.api.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuthService authService;
    
    @Autowired
    private VentaService ventaService;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody Map<String, Object> request) {
        try {
            String emailUsuario = (String) request.get("emailUsuario");
            String fechaEntregaStr = (String) request.get("fechaEntrega");
            String observaciones = (String) request.get("observaciones");
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");

            // Buscar usuario (opcional - puede existir o no)
            Optional<com.panaderia.api.entity.User> usuarioOpt = userRepository.findByEmail(emailUsuario);
            
            // Crear pedido
            Pedido pedido = new Pedido();
            pedido.setEmailUsuario(emailUsuario);
            // Solo asignar usuario si existe en la base de datos
            usuarioOpt.ifPresent(pedido::setUsuario);
            pedido.setNumeroPedido(generarNumeroPedido());
            pedido.setFechaCreacion(LocalDateTime.now());
            
            if (fechaEntregaStr != null && !fechaEntregaStr.isEmpty()) {
                pedido.setFechaEntrega(LocalDateTime.parse(fechaEntregaStr));
            }
            
            pedido.setObservaciones(observaciones);
            pedido.setEstado("RECIBIDO");

            // Procesar items del pedido
            List<PedidoItem> pedidoItems = new ArrayList<>();
            java.math.BigDecimal subtotal = java.math.BigDecimal.ZERO;

            for (Map<String, Object> itemData : items) {
                Long productId = Long.valueOf(itemData.get("productId").toString());
                Integer cantidad = Integer.valueOf(itemData.get("cantidad").toString());
                String mensajePersonalizado = (String) itemData.get("mensajePersonalizado");

                Optional<Product> productOpt = productRepository.findById(productId);
                if (!productOpt.isPresent()) {
                    continue;
                }

                Product product = productOpt.get();
                
                PedidoItem item = new PedidoItem();
                item.setPedido(pedido);
                item.setProduct(product);
                item.setCantidad(cantidad);
                item.setPrecioUnitario(product.getPrecio());
                item.setMensajePersonalizado(mensajePersonalizado);
                // Calcular subtotal usando BigDecimal
                item.setCantidad(cantidad);
                item.setSubtotal(product.getPrecio().multiply(java.math.BigDecimal.valueOf(cantidad)));

                pedidoItems.add(item);
                subtotal = subtotal.add(item.getSubtotal() != null ? item.getSubtotal() : java.math.BigDecimal.ZERO);
            }

            // Aplicar descuentos solo si el usuario existe (usar BigDecimal)
            double descuentoPorcentaje = 0;
            if (usuarioOpt.isPresent()) {
                descuentoPorcentaje = authService.calcularDescuentoTotal(emailUsuario);
            }
            java.math.BigDecimal descuentoMonto = subtotal.multiply(java.math.BigDecimal.valueOf(descuentoPorcentaje)).divide(java.math.BigDecimal.valueOf(100));
            java.math.BigDecimal total = subtotal.subtract(descuentoMonto);

            // Verificar torta gratis para estudiantes Duoc solo si el usuario existe
            boolean tortaGratis = usuarioOpt.isPresent() && authService.puedeAccederTortaGratis(emailUsuario);
            if (tortaGratis) {
                // Buscar la torta más cara y hacerla gratis
                Optional<PedidoItem> tortaMasCara = pedidoItems.stream()
                    .filter(item -> item.getProduct().getCodigo().startsWith("T"))
                    .max(Comparator.comparing(PedidoItem::getPrecioUnitario));
                
                if (tortaMasCara.isPresent()) {
                    java.math.BigDecimal descuentoTorta = tortaMasCara.get().getSubtotal() != null ? tortaMasCara.get().getSubtotal() : java.math.BigDecimal.ZERO;
                    descuentoMonto = descuentoMonto.add(descuentoTorta);
                    total = subtotal.subtract(descuentoMonto);
                }
            }

            pedido.setSubtotal(subtotal);
            pedido.setDescuentoMonto(descuentoMonto);
            pedido.setTotal(total);
            pedido.setItems(pedidoItems);

            // Guardar pedido
            Pedido pedidoGuardado = pedidoRepository.save(pedido);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pedido creado exitosamente");
            java.util.Map<String, Object> pedidoMap = new java.util.HashMap<>();
            pedidoMap.put("id", pedidoGuardado.getId());
            pedidoMap.put("numeroPedido", pedidoGuardado.getNumeroPedido());
            pedidoMap.put("estado", pedidoGuardado.getEstado());
            pedidoMap.put("subtotal", pedidoGuardado.getSubtotal());
            pedidoMap.put("descuento", pedidoGuardado.getDescuentoMonto());
            pedidoMap.put("total", pedidoGuardado.getTotal());
            pedidoMap.put("fechaCreacion", pedidoGuardado.getFechaCreacion());
            pedidoMap.put("fechaEntrega", pedidoGuardado.getFechaEntrega());
            response.put("pedido", pedidoMap);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al crear pedido: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{numeroPedido}/seguimiento")
    public ResponseEntity<?> seguimientoPedido(@PathVariable String numeroPedido) {
        try {
            Optional<Pedido> pedidoOpt = pedidoRepository.findByNumeroPedido(numeroPedido);
            if (!pedidoOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Pedido no encontrado");
                return ResponseEntity.notFound().build();
            }

            Pedido pedido = pedidoOpt.get();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            java.util.Map<String, Object> seguimiento = new java.util.HashMap<>();
            seguimiento.put("numeroPedido", pedido.getNumeroPedido());
            seguimiento.put("estado", pedido.getEstado());
            seguimiento.put("fechaCreacion", pedido.getFechaCreacion());
            seguimiento.put("fechaEntrega", pedido.getFechaEntrega());
            seguimiento.put("total", pedido.getTotal());
            seguimiento.put("items", pedido.getItems().stream().map(item -> {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("producto", item.getProduct().getNombre());
                m.put("cantidad", item.getCantidad());
                m.put("precio", item.getPrecioUnitario());
                m.put("mensajePersonalizado", item.getMensajePersonalizado() != null ? item.getMensajePersonalizado() : "");
                return m;
            }).collect(Collectors.toList()));
            response.put("pedido", seguimiento);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener seguimiento: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String nuevoEstado = request.get("estado");
            
            Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
            if (!pedidoOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Pedido no encontrado");
                return ResponseEntity.notFound().build();
            }

            Pedido pedido = pedidoOpt.get();
            pedido.setEstado(nuevoEstado);
            pedidoRepository.save(pedido);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Estado actualizado exitosamente");
            response.put("nuevoEstado", nuevoEstado);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al actualizar estado: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/usuario/{email}")
    public ResponseEntity<?> pedidosPorUsuario(@PathVariable String email) {
        try {
            List<Pedido> pedidos = pedidoRepository.findByEmailUsuarioOrderByFechaCreacionDesc(email);
            
            List<Map<String, Object>> pedidosResponse = pedidos.stream().map(pedido -> {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("id", pedido.getId());
                m.put("numeroPedido", pedido.getNumeroPedido());
                m.put("estado", pedido.getEstado());
                m.put("total", pedido.getTotal());
                m.put("fechaCreacion", pedido.getFechaCreacion());
                m.put("fechaEntrega", pedido.getFechaEntrega() != null ? pedido.getFechaEntrega() : "");
                m.put("cantidadItems", pedido.getItems().size());
                return m;
            }).collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("pedidos", pedidosResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener pedidos: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/cliente/{email}")
    public ResponseEntity<?> pedidosPorCliente(@PathVariable String email) {
        // Delegar al método existente /usuario/{email}
        return pedidosPorUsuario(email);
    }

    @GetMapping
    public ResponseEntity<?> listarPedidos() {
        try {
            List<Pedido> pedidos = pedidoRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", pedidos.size());
            response.put("pedidos", pedidos.stream().map(pedido -> Map.of(
                "id", pedido.getId(),
                "numeroPedido", pedido.getNumeroPedido(),
                "emailUsuario", pedido.getEmailUsuario(),
                "fechaCreacion", pedido.getFechaCreacion(),
                "fechaEntrega", pedido.getFechaEntrega() != null ? pedido.getFechaEntrega() : "",
                "estado", pedido.getEstado(),
                "total", pedido.getTotal(),
                "observaciones", pedido.getObservaciones() != null ? pedido.getObservaciones() : ""
            )).toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener pedidos: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Convertir pedido a venta
     * POST /api/pedidos/{id}/convertir-a-venta
     */
    @PostMapping("/{id}/convertir-a-venta")
    public ResponseEntity<?> convertirPedidoAVenta(@PathVariable Long id, @RequestBody Map<String, String> datos) {
        try {
            Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
            if (!pedidoOpt.isPresent()) {
                return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", "Pedido no encontrado"
                ));
            }
            
            Pedido pedido = pedidoOpt.get();
            
            // Verificar que el pedido esté en estado válido para conversión
            if ("ENTREGADO".equals(pedido.getEstado()) || "CANCELADO".equals(pedido.getEstado())) {
                return ResponseEntity.status(400).body(Map.of(
                    "success", false,
                    "message", "No se puede convertir un pedido " + pedido.getEstado() + " a venta"
                ));
            }
            
            // Crear datos para la venta basados en el pedido
            Map<String, Object> datosVenta = new HashMap<>();
            datosVenta.put("emailCliente", pedido.getEmailUsuario());
            datosVenta.put("nombreCliente", pedido.getUsuario() != null ? 
                pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido() : "Cliente");
            datosVenta.put("metodoPago", datos.get("metodoPago") != null ? datos.get("metodoPago") : "EFECTIVO");
            datosVenta.put("observaciones", "Venta generada desde pedido: " + pedido.getNumeroPedido() + 
                (pedido.getObservaciones() != null ? " | " + pedido.getObservaciones() : ""));
            
            // Convertir items del pedido a items de venta
            List<Map<String, Object>> itemsVenta = new ArrayList<>();
            for (PedidoItem item : pedido.getItems()) {
                Map<String, Object> itemVenta = new HashMap<>();
                itemVenta.put("productId", item.getProduct().getId());
                itemVenta.put("cantidad", item.getCantidad());
                if (item.getMensajePersonalizado() != null) {
                    itemVenta.put("mensajePersonalizado", item.getMensajePersonalizado());
                }
                itemsVenta.add(itemVenta);
            }
            datosVenta.put("items", itemsVenta);
            
            // Procesar la venta
            Venta venta = ventaService.procesarVenta(datosVenta);
            
            // Actualizar estado del pedido
            pedido.setEstado("ENTREGADO");
            pedido.setFechaEntregaReal(LocalDateTime.now());
            pedidoRepository.save(pedido);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pedido convertido a venta exitosamente");
            response.put("pedidoId", pedido.getId());
            response.put("numeroPedido", pedido.getNumeroPedido());
            response.put("ventaId", venta.getId());
            response.put("numeroVenta", venta.getNumeroVenta());
            response.put("total", venta.getTotal());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of(
                "success", false,
                "message", "Error al convertir pedido a venta: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Sistema de pedidos funcionando correctamente");
        response.put("estadosDisponibles", Arrays.asList(
            "RECIBIDO", "EN_PREPARACION", "LISTO", "EN_ENTREGA", "ENTREGADO", "CANCELADO"
        ));
        response.put("endpoints", Map.of(
            "listar", "GET /api/pedidos",
            "crear", "POST /api/pedidos",
            "seguimiento", "GET /api/pedidos/{numeroPedido}/seguimiento",
            "actualizarEstado", "PUT /api/pedidos/{id}/estado",
            "pedidosUsuario", "GET /api/pedidos/usuario/{email}",
            "convertirAVenta", "POST /api/pedidos/{id}/convertir-a-venta"
        ));
        
        return ResponseEntity.ok(response);
    }

    private String generarNumeroPedido() {
        return "PAN-" + System.currentTimeMillis();
    }
}