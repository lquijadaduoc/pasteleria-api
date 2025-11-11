package com.panaderia.api.repository;

import com.panaderia.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    // Consultas temporalmente comentadas para evitar errores de inicialización
    // @Query("SELECT u FROM User u WHERE u.estudianteDuoc = true")
    // List<User> findAllEstudiantesDuoc();
    
    // @Query("SELECT u FROM User u WHERE u.descuentoAdultoMayor = true")
    // List<User> findAllUsuariosConDescuentoEdad();
    
    // @Query("SELECT u FROM User u WHERE u.descuentoFelices50 = true")
    // List<User> findAllUsuariosConCodigoFelices50();
    
    // @Query("SELECT u FROM User u WHERE u.email LIKE %:domain")
    // List<User> findByEmailDomain(String domain);
}