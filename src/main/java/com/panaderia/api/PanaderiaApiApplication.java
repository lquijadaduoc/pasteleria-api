package com.panaderia.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Spring Boot para la API de Panadería
 * 
 * Esta aplicación maneja:
 * - Gestión de productos de panadería
 * - Control de stocks e inventario
 * - Administración de bodegas
 * - Carrito de compras para tortas
 */
@SpringBootApplication
public class PanaderiaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PanaderiaApiApplication.class, args);
        System.out.println("🍰 Panadería API iniciada correctamente!");
        System.out.println("📍 Documentación disponible en: http://localhost:8080/");
    }
}