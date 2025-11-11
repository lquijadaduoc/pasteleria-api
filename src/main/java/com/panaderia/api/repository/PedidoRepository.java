package com.panaderia.api.repository;

import com.panaderia.api.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    Optional<Pedido> findByNumeroPedido(String numeroPedido);
    
    @Query("SELECT p FROM Pedido p WHERE p.usuario.email = ?1 ORDER BY p.fechaPedido DESC")
    List<Pedido> findByEmailUsuarioOrderByFechaCreacionDesc(String emailUsuario);
    
    List<Pedido> findByEstado(String estado);
    
    @Query("SELECT p FROM Pedido p WHERE p.fechaEntregaSolicitada BETWEEN ?1 AND ?2")
    List<Pedido> findPedidosByFechaEntrega(LocalDate fechaInicio, LocalDate fechaFin);
    
    @Query("SELECT p FROM Pedido p WHERE p.estado = ?1 AND p.fechaEntregaSolicitada <= ?2")
    List<Pedido> findPedidosParaNotificar(String estado, LocalDate fecha);
    
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.usuario.email = ?1")
    Long countPedidosByUsuario(String emailUsuario);
    
    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.usuario.email = ?1 AND p.estado != 'CANCELADO'")
    Double sumTotalByUsuario(String emailUsuario);
}