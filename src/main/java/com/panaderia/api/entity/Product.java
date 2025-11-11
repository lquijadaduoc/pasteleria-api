package com.panaderia.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un producto de la panadería
 */
@Entity
@Table(name = "productos")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El código del producto es obligatorio")
    @Column(name = "codigo", nullable = false, unique = true, length = 10)
    private String codigo;
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    
    @Column(name = "descripcion", length = 500)
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "La categoría es obligatoria")
    @Column(name = "categoria", nullable = false)
    private CategoriaProducto categoria;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_torta")
    private FormaTorta formaTorta;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tamano")
    private TamañoProducto tamaño;
    
    @Column(name = "personalizable", nullable = false)
    private Boolean personalizable = false;
    
    @Column(name = "mensaje_especial", nullable = false)
    private Boolean mensajeEspecial = false;
    
    @Column(name = "sin_azucar", nullable = false)
    private Boolean sinAzucar = false;
    
    @Column(name = "sin_gluten", nullable = false)
    private Boolean sinGluten = false;
    
    @Column(name = "vegano", nullable = false)
    private Boolean vegano = false;
    
    @Column(name = "historia_origen", length = 1000)
    private String historiaOrigen;
    
    @NotNull(message = "El stock es obligatorio")
    @Column(name = "stock", nullable = false)
    private Integer stock = 0;
    
    @Column(name = "stock_minimo")
    private Integer stockMinimo = 5;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    // Enums
    public enum CategoriaProducto {
        TORTAS_CUADRADAS, TORTAS_CIRCULARES, POSTRES_INDIVIDUALES, 
        PRODUCTOS_SIN_AZUCAR, PASTELERIA_TRADICIONAL, PRODUCTOS_SIN_GLUTEN, 
        PRODUCTOS_VEGANA, TORTAS_ESPECIALES
    }
    
    public enum FormaTorta {
        CUADRADA, CIRCULAR, RECTANGULAR, INDIVIDUAL
    }
    
    public enum TamañoProducto {
        PEQUEÑO, MEDIANO, GRANDE, FAMILIAR, INDIVIDUAL
    }
    
    // Constructores
    public Product() {
        this.fechaCreacion = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    
    public CategoriaProducto getCategoria() { return categoria; }
    public void setCategoria(CategoriaProducto categoria) { this.categoria = categoria; }
    
    public FormaTorta getFormaTorta() { return formaTorta; }
    public void setFormaTorta(FormaTorta formaTorta) { this.formaTorta = formaTorta; }
    
    public TamañoProducto getTamaño() { return tamaño; }
    public void setTamaño(TamañoProducto tamaño) { this.tamaño = tamaño; }
    
    public Boolean getPersonalizable() { return personalizable; }
    public void setPersonalizable(Boolean personalizable) { this.personalizable = personalizable; }
    
    public Boolean getMensajeEspecial() { return mensajeEspecial; }
    public void setMensajeEspecial(Boolean mensajeEspecial) { this.mensajeEspecial = mensajeEspecial; }
    
    public Boolean getSinAzucar() { return sinAzucar; }
    public void setSinAzucar(Boolean sinAzucar) { this.sinAzucar = sinAzucar; }
    
    public Boolean getSinGluten() { return sinGluten; }
    public void setSinGluten(Boolean sinGluten) { this.sinGluten = sinGluten; }
    
    public Boolean getVegano() { return vegano; }
    public void setVegano(Boolean vegano) { this.vegano = vegano; }
    
    public String getHistoriaOrigen() { return historiaOrigen; }
    public void setHistoriaOrigen(String historiaOrigen) { this.historiaOrigen = historiaOrigen; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    // Método helper para verificar si es torta
    public Boolean getEsTorta() {
        return categoria == CategoriaProducto.TORTAS_CUADRADAS || 
               categoria == CategoriaProducto.TORTAS_CIRCULARES || 
               categoria == CategoriaProducto.TORTAS_ESPECIALES;
    }
    
    // Métodos helper para stock
    public Boolean tieneStock() {
        return stock != null && stock > 0;
    }
    
    public Boolean stockBajo() {
        return stock != null && stockMinimo != null && stock <= stockMinimo;
    }
    
    public void reducirStock(Integer cantidad) {
        if (stock != null && cantidad != null) {
            this.stock = Math.max(0, this.stock - cantidad);
        }
    }
    
    public void aumentarStock(Integer cantidad) {
        if (stock == null) {
            this.stock = cantidad != null ? cantidad : 0;
        } else {
            this.stock += cantidad != null ? cantidad : 0;
        }
    }
}