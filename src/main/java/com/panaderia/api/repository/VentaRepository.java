package com.panaderia.api.repository;

import com.panaderia.api.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    /**
     * Buscar ventas por email del cliente
     */
    List<Venta> findByEmailClienteContainingIgnoreCase(String emailCliente);
    
    /**
     * Buscar ventas por usuario
     */
    List<Venta> findByUsuario_Id(Long usuarioId);
    
    /**
     * Buscar venta por número
     */
    Optional<Venta> findByNumeroVenta(String numeroVenta);
    
    /**
     * Buscar ventas por fecha
     */
    @Query("SELECT v FROM Venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin ORDER BY v.fechaVenta DESC")
    List<Venta> findByFechaBetween(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Ventas del día actual
     */
    @Query("SELECT v FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE ORDER BY v.fechaVenta DESC")
    List<Venta> findVentasDelDia();
    
    /**
     * Ventas de la semana actual
     */
    @Query("SELECT v FROM Venta v WHERE YEARWEEK(v.fechaVenta) = YEARWEEK(NOW()) ORDER BY v.fechaVenta DESC")
    List<Venta> findVentasDeLaSemana();
    
    /**
     * Ventas del mes actual
     */
    @Query("SELECT v FROM Venta v WHERE YEAR(v.fechaVenta) = YEAR(NOW()) AND MONTH(v.fechaVenta) = MONTH(NOW()) ORDER BY v.fechaVenta DESC")
    List<Venta> findVentasDelMes();
    
    /**
     * Total de ventas por estado
     */
    @Query("SELECT COUNT(v) FROM Venta v WHERE v.estado = :estado")
    Long countByEstado(@Param("estado") Venta.EstadoVenta estado);
    
    /**
     * Suma total de ventas completadas
     */
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.estado = 'COMPLETADA'")
    java.math.BigDecimal sumTotalVentasCompletadas();
    
    /**
     * Suma total de ventas del día
     */
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE DATE(v.fechaVenta) = CURRENT_DATE AND v.estado = 'COMPLETADA'")
    java.math.BigDecimal sumTotalVentasDelDia();
    
    /**
     * Top productos más vendidos
     */
    @Query("SELECT vi.product.nombre, SUM(vi.cantidad) as totalVendido " +
           "FROM VentaItem vi JOIN vi.venta v " +
           "WHERE v.estado = 'COMPLETADA' " +
           "GROUP BY vi.product.id, vi.product.nombre " +
           "ORDER BY totalVendido DESC")
    List<Object[]> findTopProductosMasVendidos();
}