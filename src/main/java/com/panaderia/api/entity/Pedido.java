package com.panaderia.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

/**
 * Entidad que representa un pedido realizado por un usuario
 * Incluye seguimiento de estado y notificaciones
 */
@Entity
@Table(name = "pedidos")
public class Pedido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El número de pedido es obligatorio")
    @Column(name = "numero_pedido", nullable = false, unique = true, length = 20)
    private String numeroPedido;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User usuario;
    
    @Column(name = "fecha_pedido", nullable = false)
    private LocalDateTime fechaPedido;
    
    @Column(name = "fecha_entrega_solicitada")
    private LocalDate fechaEntregaSolicitada;
    
    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal;
    
    @NotNull(message = "El subtotal es obligatorio")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;
    
    @Column(name = "costo_envio", precision = 10, scale = 2)
    private BigDecimal costoEnvio = BigDecimal.ZERO;
    
    @NotNull(message = "El total es obligatorio")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado = EstadoPedido.RECIBIDO;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entrega", nullable = false, length = 20)
    private TipoEntrega tipoEntrega = TipoEntrega.RETIRO_TIENDA;
    
    @Size(max = 300, message = "Las observaciones no pueden exceder 300 caracteres")
    @Column(length = 300)
    private String observaciones;
    
    @Size(max = 200, message = "La dirección de entrega no puede exceder 200 caracteres")
    @Column(name = "direccion_entrega", length = 200)
    private String direccionEntrega;
    
    @Size(max = 100, message = "El mensaje especial no puede exceder 100 caracteres")
    @Column(name = "mensaje_especial", length = 100)
    private String mensajeEspecial;
    
    @Column(name = "numero_boleta", length = 20)
    private String numeroBoleta;
    
    @Column(name = "codigo_seguimiento", length = 50)
    private String codigoSeguimiento;
    
    @Column(name = "notificacion_enviada")
    private Boolean notificacionEnviada = false;
    
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PedidoItem> items;
    
    // Enums
    public enum EstadoPedido {
        RECIBIDO("Pedido recibido"),
        CONFIRMADO("Pedido confirmado"),
        EN_PREPARACION("En preparación"),
        LISTO_PARA_ENTREGA("Listo para entrega"),
        EN_TRANSITO("En tránsito"),
        ENTREGADO("Entregado"),
        CANCELADO("Cancelado");
        
        private final String descripcion;
        
        EstadoPedido(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    public enum TipoEntrega {
        RETIRO_TIENDA("Retiro en tienda"),
        DELIVERY("Delivery a domicilio"),
        ENVIO_NACIONAL("Envío nacional");
        
        private final String descripcion;
        
        TipoEntrega(String descripcion) {
            this.descripcion = descripcion;
        }
        
        public String getDescripcion() {
            return descripcion;
        }
    }
    
    // Constructores
    public Pedido() {
        this.fechaPedido = LocalDateTime.now();
        this.numeroPedido = generarNumeroPedido();
    }
    
    public Pedido(User usuario, TipoEntrega tipoEntrega) {
        this();
        this.usuario = usuario;
        this.tipoEntrega = tipoEntrega;
    }
    
    // Métodos de negocio
    private String generarNumeroPedido() {
        return "PED" + System.currentTimeMillis();
    }
    
    public void calcularTotal() {
        this.total = subtotal.subtract(descuento).add(costoEnvio);
    }
    
    public void avanzarEstado() {
        switch (this.estado) {
            case RECIBIDO -> this.estado = EstadoPedido.CONFIRMADO;
            case CONFIRMADO -> this.estado = EstadoPedido.EN_PREPARACION;
            case EN_PREPARACION -> this.estado = EstadoPedido.LISTO_PARA_ENTREGA;
            case LISTO_PARA_ENTREGA -> this.estado = EstadoPedido.EN_TRANSITO;
            case EN_TRANSITO -> {
                this.estado = EstadoPedido.ENTREGADO;
                this.fechaEntregaReal = LocalDateTime.now();
            }
        }
    }
    
    public boolean puedeSerCancelado() {
        return estado == EstadoPedido.RECIBIDO || estado == EstadoPedido.CONFIRMADO;
    }
    
    public boolean requiereEnvio() {
        return tipoEntrega == TipoEntrega.DELIVERY || tipoEntrega == TipoEntrega.ENVIO_NACIONAL;
    }
    
    public String generarCodigoSeguimiento() {
        if (this.codigoSeguimiento == null) {
            this.codigoSeguimiento = "TRK" + this.id + System.currentTimeMillis();
        }
        return this.codigoSeguimiento;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNumeroPedido() { return numeroPedido; }
    public void setNumeroPedido(String numeroPedido) { this.numeroPedido = numeroPedido; }
    
    public User getUsuario() { return usuario; }
    public void setUsuario(User usuario) { this.usuario = usuario; }
    
    public LocalDateTime getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(LocalDateTime fechaPedido) { this.fechaPedido = fechaPedido; }
    
    public LocalDate getFechaEntregaSolicitada() { return fechaEntregaSolicitada; }
    public void setFechaEntregaSolicitada(LocalDate fechaEntregaSolicitada) { this.fechaEntregaSolicitada = fechaEntregaSolicitada; }
    
    public LocalDateTime getFechaEntregaReal() { return fechaEntregaReal; }
    public void setFechaEntregaReal(LocalDateTime fechaEntregaReal) { this.fechaEntregaReal = fechaEntregaReal; }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public BigDecimal getDescuento() { return descuento; }
    public void setDescuento(BigDecimal descuento) { this.descuento = descuento; }
    
    public BigDecimal getCostoEnvio() { return costoEnvio; }
    public void setCostoEnvio(BigDecimal costoEnvio) { this.costoEnvio = costoEnvio; }
    
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    
    public TipoEntrega getTipoEntrega() { return tipoEntrega; }
    public void setTipoEntrega(TipoEntrega tipoEntrega) { this.tipoEntrega = tipoEntrega; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
    
    public String getMensajeEspecial() { return mensajeEspecial; }
    public void setMensajeEspecial(String mensajeEspecial) { this.mensajeEspecial = mensajeEspecial; }
    
    public String getNumeroBoleta() { return numeroBoleta; }
    public void setNumeroBoleta(String numeroBoleta) { this.numeroBoleta = numeroBoleta; }
    
    public String getCodigoSeguimiento() { return codigoSeguimiento; }
    public void setCodigoSeguimiento(String codigoSeguimiento) { this.codigoSeguimiento = codigoSeguimiento; }
    
    public Boolean getNotificacionEnviada() { return notificacionEnviada; }
    public void setNotificacionEnviada(Boolean notificacionEnviada) { this.notificacionEnviada = notificacionEnviada; }
    
    public List<PedidoItem> getItems() { return items; }
    public void setItems(List<PedidoItem> items) { this.items = items; }
    
    // Métodos de conveniencia para compatibilidad
    public String getEmailUsuario() { return usuario != null ? usuario.getEmail() : null; }
    public void setEmailUsuario(String emailUsuario) { /* Este campo se maneja a través de la relación User */ }
    
    public LocalDateTime getFechaCreacion() { return fechaPedido; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaPedido = fechaCreacion; }
    
    public LocalDateTime getFechaEntrega() { return fechaEntregaReal; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntregaReal = fechaEntrega; }
    
    public BigDecimal getDescuentoMonto() { return descuento; }
    public void setDescuentoMonto(BigDecimal descuentoMonto) { this.descuento = descuentoMonto; }
    
    // Método para setear estado desde String
    public void setEstado(String estadoStr) {
        try {
            this.estado = EstadoPedido.valueOf(estadoStr);
        } catch (IllegalArgumentException e) {
            // Manejar conversión de estados antiguos
            switch (estadoStr) {
                case "RECIBIDO": this.estado = EstadoPedido.RECIBIDO; break;
                case "EN_PREPARACION": this.estado = EstadoPedido.EN_PREPARACION; break;
                case "LISTO": this.estado = EstadoPedido.LISTO_PARA_ENTREGA; break;
                case "EN_ENTREGA": this.estado = EstadoPedido.EN_TRANSITO; break;
                case "ENTREGADO": this.estado = EstadoPedido.ENTREGADO; break;
                case "CANCELADO": this.estado = EstadoPedido.CANCELADO; break;
                default: this.estado = EstadoPedido.RECIBIDO; break;
            }
        }
    }
}