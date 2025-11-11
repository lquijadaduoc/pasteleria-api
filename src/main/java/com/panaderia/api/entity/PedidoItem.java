package com.panaderia.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Entidad que representa un item individual dentro de un pedido
 */
@Entity
@Table(name = "pedido_items")
public class PedidoItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Column(nullable = false)
    private Integer cantidad;
    
    @NotNull(message = "El precio unitario es obligatorio")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;
    
    @Size(max = 200, message = "La personalización no puede exceder 200 caracteres")
    @Column(name = "personalizacion", length = 200)
    private String personalizacion;
    
    @Size(max = 100, message = "El mensaje especial no puede exceder 100 caracteres")
    @Column(name = "mensaje_especial", length = 100)
    private String mensajeEspecial;
    
    @Column(name = "es_regalo")
    private Boolean esRegalo = false;
    
    @Column(name = "aplicar_descuento")
    private Boolean aplicarDescuento = true;
    
    // Constructores
    public PedidoItem() {}
    
    public PedidoItem(Pedido pedido, Product product, Integer cantidad) {
        this.pedido = pedido;
        this.product = product;
        this.cantidad = cantidad;
        this.precioUnitario = product.getPrecio();
        calcularSubtotal();
    }
    
    // Métodos de negocio
    public void calcularSubtotal() {
        if (precioUnitario != null && cantidad != null) {
            this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }
    
    public void actualizarCantidad(Integer nuevaCantidad) {
        this.cantidad = nuevaCantidad;
        calcularSubtotal();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { 
        this.cantidad = cantidad;
        calcularSubtotal();
    }
    
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { 
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }
    
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    public String getPersonalizacion() { return personalizacion; }
    public void setPersonalizacion(String personalizacion) { this.personalizacion = personalizacion; }
    
    public String getMensajeEspecial() { return mensajeEspecial; }
    public void setMensajeEspecial(String mensajeEspecial) { this.mensajeEspecial = mensajeEspecial; }
    
    public Boolean getEsRegalo() { return esRegalo; }
    public void setEsRegalo(Boolean esRegalo) { this.esRegalo = esRegalo; }
    
    public Boolean getAplicarDescuento() { return aplicarDescuento; }
    public void setAplicarDescuento(Boolean aplicarDescuento) { this.aplicarDescuento = aplicarDescuento; }
    
    // Métodos de conveniencia para compatibilidad
    public String getMensajePersonalizado() { return personalizacion; }
    public void setMensajePersonalizado(String mensajePersonalizado) { this.personalizacion = mensajePersonalizado; }
}