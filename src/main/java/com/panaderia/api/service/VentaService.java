package com.panaderia.api.service;

import com.panaderia.api.entity.*;
import com.panaderia.api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VentaService {
    
    @Autowired
    private VentaRepository ventaRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthService authService;
    
    /**
     * Procesar una nueva venta
     */
    @Transactional
    public Venta procesarVenta(Map<String, Object> datosVenta) {
        try {
            // Crear la venta
            Venta venta = new Venta();
            venta.setNumeroVenta(generarNumeroVenta());
            venta.setFechaVenta(LocalDateTime.now());
            
            // Datos del cliente
            String emailCliente = (String) datosVenta.get("emailCliente");
            String nombreCliente = (String) datosVenta.get("nombreCliente");
            String metodoPago = (String) datosVenta.get("metodoPago");
            String observaciones = (String) datosVenta.get("observaciones");
            
            venta.setEmailCliente(emailCliente);
            venta.setNombreCliente(nombreCliente);
            venta.setObservaciones(observaciones);
            
            if (metodoPago != null) {
                try {
                    venta.setMetodoPago(Venta.MetodoPago.valueOf(metodoPago.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    venta.setMetodoPago(Venta.MetodoPago.EFECTIVO);
                }
            }
            
            // Buscar usuario si existe
            if (emailCliente != null) {
                Optional<User> usuario = userRepository.findByEmail(emailCliente);
                usuario.ifPresent(venta::setUsuario);
            }
            
            // Procesar items
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) datosVenta.get("items");
            List<VentaItem> ventaItems = new ArrayList<>();
            BigDecimal subtotal = BigDecimal.ZERO;
            
            for (Map<String, Object> itemData : itemsData) {
                Long productId = Long.valueOf(itemData.get("productId").toString());
                Integer cantidad = Integer.valueOf(itemData.get("cantidad").toString());
                String mensajePersonalizado = (String) itemData.get("mensajePersonalizado");
                
                Optional<Product> productOpt = productRepository.findById(productId);
                if (!productOpt.isPresent()) {
                    throw new RuntimeException("Producto no encontrado: " + productId);
                }
                
                Product product = productOpt.get();
                
                // Verificar stock disponible
                if (!product.tieneStock() || product.getStock() < cantidad) {
                    throw new RuntimeException("Stock insuficiente para producto: " + product.getNombre() + 
                                             " (Disponible: " + product.getStock() + ", Solicitado: " + cantidad + ")");
                }
                
                // Crear item de venta
                VentaItem item = new VentaItem();
                item.setVenta(venta);
                item.setProduct(product);
                item.setCantidad(cantidad);
                item.setPrecioUnitario(product.getPrecio());
                item.setMensajePersonalizado(mensajePersonalizado);
                item.calcularSubtotal();
                
                ventaItems.add(item);
                subtotal = subtotal.add(item.getSubtotal());
                
                // Reducir stock del producto
                product.reducirStock(cantidad);
                productRepository.save(product);
            }
            
            // Aplicar descuentos si es usuario registrado
            BigDecimal descuento = BigDecimal.ZERO;
            if (venta.getUsuario() != null) {
                double descuentoPorcentaje = authService.calcularDescuentoTotal(emailCliente);
                descuento = subtotal.multiply(BigDecimal.valueOf(descuentoPorcentaje))
                                  .divide(BigDecimal.valueOf(100));
            }
            
            // Calcular total
            BigDecimal total = subtotal.subtract(descuento);
            
            venta.setSubtotal(subtotal);
            venta.setDescuento(descuento);
            venta.setTotal(total);
            venta.setItems(ventaItems);
            venta.setEstado(Venta.EstadoVenta.COMPLETADA);
            
            // Guardar venta
            Venta ventaGuardada = ventaRepository.save(venta);
            
            return ventaGuardada;
            
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar venta: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtener todas las ventas
     */
    public List<Venta> obtenerTodasLasVentas() {
        return ventaRepository.findAll();
    }
    
    /**
     * Obtener venta por ID
     */
    public Optional<Venta> obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id);
    }
    
    /**
     * Obtener ventas por usuario
     */
    public List<Venta> obtenerVentasPorUsuario(Long usuarioId) {
        return ventaRepository.findByUsuario_Id(usuarioId);
    }
    
    /**
     * Obtener ventas del día
     */
    public List<Venta> obtenerVentasDelDia() {
        return ventaRepository.findVentasDelDia();
    }
    
    /**
     * Obtener estadísticas de ventas
     */
    public Map<String, Object> obtenerEstadisticasVentas() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        estadisticas.put("totalVentas", ventaRepository.count());
        estadisticas.put("ventasCompletadas", ventaRepository.countByEstado(Venta.EstadoVenta.COMPLETADA));
        estadisticas.put("ventasCanceladas", ventaRepository.countByEstado(Venta.EstadoVenta.CANCELADA));
        estadisticas.put("montoTotalVentas", ventaRepository.sumTotalVentasCompletadas());
        estadisticas.put("montoVentasDelDia", ventaRepository.sumTotalVentasDelDia());
        estadisticas.put("ventasDelDia", ventaRepository.findVentasDelDia().size());
        estadisticas.put("ventasDeLaSemana", ventaRepository.findVentasDeLaSemana().size());
        estadisticas.put("ventasDelMes", ventaRepository.findVentasDelMes().size());
        
        // Top productos
        List<Object[]> topProductos = ventaRepository.findTopProductosMasVendidos();
        List<Map<String, Object>> productos = new ArrayList<>();
        for (Object[] row : topProductos) {
            Map<String, Object> producto = new HashMap<>();
            producto.put("nombre", row[0]);
            producto.put("cantidadVendida", row[1]);
            productos.add(producto);
        }
        estadisticas.put("topProductos", productos);
        
        return estadisticas;
    }
    
    /**
     * Cancelar venta
     */
    @Transactional
    public Venta cancelarVenta(Long ventaId, String motivo) {
        Optional<Venta> ventaOpt = ventaRepository.findById(ventaId);
        if (!ventaOpt.isPresent()) {
            throw new RuntimeException("Venta no encontrada");
        }
        
        Venta venta = ventaOpt.get();
        if (venta.getEstado() != Venta.EstadoVenta.COMPLETADA) {
            throw new RuntimeException("Solo se pueden cancelar ventas completadas");
        }
        
        // Devolver stock a los productos
        for (VentaItem item : venta.getItems()) {
            Product product = item.getProduct();
            product.aumentarStock(item.getCantidad());
            productRepository.save(product);
        }
        
        venta.setEstado(Venta.EstadoVenta.CANCELADA);
        if (motivo != null) {
            venta.setObservaciones((venta.getObservaciones() != null ? venta.getObservaciones() + " | " : "") + 
                                  "CANCELADA: " + motivo);
        }
        
        return ventaRepository.save(venta);
    }
    
    /**
     * Generar número de venta único
     */
    private String generarNumeroVenta() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "V" + timestamp;
    }
}