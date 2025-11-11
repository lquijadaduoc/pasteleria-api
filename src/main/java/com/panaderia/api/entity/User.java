package com.panaderia.api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Entidad que representa un usuario del sistema
 * Incluye sistema de descuentos especiales
 */
@Entity
@Table(name = "usuarios")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellido;
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;
    
    @Size(max = 15, message = "El teléfono no puede exceder 15 caracteres")
    @Column(length = 15)
    private String telefono;
    
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;
    
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    @Column(length = 200)
    private String direccion;
    
    // Campos para descuentos especiales
    @Column(name = "descuento_adulto_mayor")
    private Boolean descuentoAdultoMayor = false;
    
    @Column(name = "descuento_felices50")
    private Boolean descuentoFelices50 = false;
    
    @Column(name = "estudiante_duoc")
    private Boolean estudianteDuoc = false;
    
    @Column(name = "torta_gratis_cumpleanos")
    private Boolean tortaGratisCumpleanos = false;
    
    @Column(name = "codigo_registro", length = 20)
    private String codigoRegistro;
    
    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolUsuario rol = RolUsuario.CLIENTE;
    
    // Enum para roles
    public enum RolUsuario {
        CLIENTE, ADMIN, EMPLEADO
    }
    
    // Constructores
    public User() {
        this.fechaRegistro = LocalDateTime.now();
    }
    
    public User(String nombre, String apellido, String email, String password, LocalDate fechaNacimiento) {
        this();
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.fechaNacimiento = fechaNacimiento;
        
        // Verificar si es mayor de 50 años
        if (fechaNacimiento != null) {
            int edad = LocalDate.now().getYear() - fechaNacimiento.getYear();
            this.descuentoAdultoMayor = edad >= 50;
        }
    }
    
    // Métodos de negocio
    public int getEdad() {
        if (fechaNacimiento == null) return 0;
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }
    
    public boolean esCumpleanos() {
        if (fechaNacimiento == null) return false;
        LocalDate hoy = LocalDate.now();
        return fechaNacimiento.getMonth() == hoy.getMonth() && 
               fechaNacimiento.getDayOfMonth() == hoy.getDayOfMonth();
    }
    
    public boolean tieneDescuentoAdultoMayor() {
        return descuentoAdultoMayor != null && descuentoAdultoMayor;
    }
    
    public boolean tieneDescuentoFelices50() {
        return descuentoFelices50 != null && descuentoFelices50;
    }
    
    public boolean esEstudianteDuoc() {
        return estudianteDuoc != null && estudianteDuoc;
    }
    
    public BigDecimal calcularDescuento(BigDecimal precioOriginal) {
        BigDecimal descuento = BigDecimal.ZERO;
        
        // Descuento adulto mayor 50%
        if (tieneDescuentoAdultoMayor()) {
            descuento = descuento.add(precioOriginal.multiply(new BigDecimal("0.50")));
        }
        
        // Descuento FELICES50 10%
        if (tieneDescuentoFelices50()) {
            descuento = descuento.add(precioOriginal.multiply(new BigDecimal("0.10")));
        }
        
        return descuento;
    }
    
    public boolean puedeRecibirTortaGratis() {
        return esEstudianteDuoc() && esCumpleanos() && !tortaGratisCumpleanos;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { 
        this.fechaNacimiento = fechaNacimiento;
        // Recalcular descuento de adulto mayor
        if (fechaNacimiento != null) {
            int edad = LocalDate.now().getYear() - fechaNacimiento.getYear();
            this.descuentoAdultoMayor = edad >= 50;
        }
    }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public Boolean getDescuentoAdultoMayor() { return descuentoAdultoMayor; }
    public void setDescuentoAdultoMayor(Boolean descuentoAdultoMayor) { this.descuentoAdultoMayor = descuentoAdultoMayor; }
    
    public Boolean getDescuentoFelices50() { return descuentoFelices50; }
    public void setDescuentoFelices50(Boolean descuentoFelices50) { this.descuentoFelices50 = descuentoFelices50; }
    
    public Boolean getEstudianteDuoc() { return estudianteDuoc; }
    public void setEstudianteDuoc(Boolean estudianteDuoc) { this.estudianteDuoc = estudianteDuoc; }
    
    public Boolean getTortaGratisCumpleanos() { return tortaGratisCumpleanos; }
    public void setTortaGratisCumpleanos(Boolean tortaGratisCumpleanos) { this.tortaGratisCumpleanos = tortaGratisCumpleanos; }
    
    public String getCodigoRegistro() { return codigoRegistro; }
    public void setCodigoRegistro(String codigoRegistro) { this.codigoRegistro = codigoRegistro; }
    
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }
    
    // Métodos de conveniencia para compatibilidad con el código existente
    public boolean isDescuentoPorEdad() { return this.descuentoAdultoMayor != null ? this.descuentoAdultoMayor : false; }
    public void setDescuentoPorEdad(boolean descuentoPorEdad) { this.descuentoAdultoMayor = descuentoPorEdad; }
    
    public int getPorcentajeDescuentoEdad() { return isDescuentoPorEdad() ? 50 : 0; }
    public void setPorcentajeDescuentoEdad(int porcentaje) { /* Se calcula automáticamente */ }
    
    public boolean isEsEstudianteDuoc() { return this.estudianteDuoc != null ? this.estudianteDuoc : false; }
    public void setEsEstudianteDuoc(boolean esEstudianteDuoc) { this.estudianteDuoc = esEstudianteDuoc; }
    
    public boolean isCodigoFelices50Usado() { return this.descuentoFelices50 != null ? this.descuentoFelices50 : false; }
    public void setCodigoFelices50Usado(boolean codigoFelices50Usado) { this.descuentoFelices50 = codigoFelices50Usado; }
    
    public int getPorcentajeDescuentoFelices50() { return isCodigoFelices50Usado() ? 10 : 0; }
    public void setPorcentajeDescuentoFelices50(int porcentaje) { /* Se calcula automáticamente */ }
}