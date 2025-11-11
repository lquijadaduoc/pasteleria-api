package com.panaderia.api.service;

import com.panaderia.api.entity.Product;
import com.panaderia.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

/**
 * Servicio para inicializar la base de datos con productos predefinidos
 */
@Service
public class DataInitializationService implements CommandLineRunner {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public void run(String... args) throws Exception {
        if (productRepository.count() == 0) {
            inicializarProductos();
        }
    }
    
    private void inicializarProductos() {
        // Tortas Cuadradas
        crearProducto("TC001", "Torta Cuadrada de Chocolate", 
                     "Deliciosa torta de chocolate con capas de ganache y un toque de avellanas. Personalizable con mensajes especiales.",
                     new BigDecimal("45000"), Product.CategoriaProducto.TORTAS_CUADRADAS,
                     Product.FormaTorta.CUADRADA, Product.TamañoProducto.MEDIANO, true, true);
        
        crearProducto("TC002", "Torta Cuadrada de Frutas",
                     "Una mezcla de frutas frescas y crema chantilly sobre un suave bizcocho de vainilla, ideal para celebraciones.",
                     new BigDecimal("50000"), Product.CategoriaProducto.TORTAS_CUADRADAS,
                     Product.FormaTorta.CUADRADA, Product.TamañoProducto.MEDIANO, true, true);
        
        // Tortas Circulares
        crearProducto("TT001", "Torta Circular de Vainilla",
                     "Bizcocho de vainilla clásico relleno con crema pastelera y cubierto con un glaseado dulce, perfecto para cualquier ocasión.",
                     new BigDecimal("40000"), Product.CategoriaProducto.TORTAS_CIRCULARES,
                     Product.FormaTorta.CIRCULAR, Product.TamañoProducto.MEDIANO, true, true);
        
        crearProducto("TT002", "Torta Circular de Manjar",
                     "Torta tradicional chilena con manjar y nueces, un deleite para los amantes de los sabores dulces y clásicos.",
                     new BigDecimal("42000"), Product.CategoriaProducto.TORTAS_CIRCULARES,
                     Product.FormaTorta.CIRCULAR, Product.TamañoProducto.MEDIANO, true, true);
        
        // Postres Individuales
        crearProducto("PI001", "Mousse de Chocolate",
                     "Postre individual cremoso y suave, hecho con chocolate de alta calidad, ideal para los amantes del chocolate.",
                     new BigDecimal("5000"), Product.CategoriaProducto.POSTRES_INDIVIDUALES,
                     null, Product.TamañoProducto.INDIVIDUAL, false, false);
        
        crearProducto("PI002", "Tiramisú Clásico",
                     "Un postre italiano individual con capas de café, mascarpone y cacao, perfecto para finalizar cualquier comida.",
                     new BigDecimal("5500"), Product.CategoriaProducto.POSTRES_INDIVIDUALES,
                     null, Product.TamañoProducto.INDIVIDUAL, false, false);
        
        // Productos Sin Azúcar
        crearProducto("PSA001", "Torta Sin Azúcar de Naranja",
                     "Torta ligera y deliciosa, endulzada naturalmente, ideal para quienes buscan opciones más saludables.",
                     new BigDecimal("48000"), Product.CategoriaProducto.PRODUCTOS_SIN_AZUCAR,
                     Product.FormaTorta.CIRCULAR, Product.TamañoProducto.MEDIANO, true, true, true);
        
        crearProducto("PSA002", "Cheesecake Sin Azúcar",
                     "Suave y cremoso, este cheesecake es una opción perfecta para disfrutar sin culpa.",
                     new BigDecimal("47000"), Product.CategoriaProducto.PRODUCTOS_SIN_AZUCAR,
                     Product.FormaTorta.CIRCULAR, Product.TamañoProducto.MEDIANO, false, false, true);
        
        // Pastelería Tradicional
        crearProducto("PT001", "Empanada de Manzana",
                     "Pastelería tradicional rellena de manzanas especiadas, perfecta para un dulce desayuno o merienda.",
                     new BigDecimal("3000"), Product.CategoriaProducto.PASTELERIA_TRADICIONAL,
                     null, Product.TamañoProducto.INDIVIDUAL, false, false);
        
        crearProducto("PT002", "Tarta de Santiago",
                     "Tradicional tarta española hecha con almendras, azúcar, y huevos, una delicia para los amantes de los postres clásicos.",
                     new BigDecimal("6000"), Product.CategoriaProducto.PASTELERIA_TRADICIONAL,
                     Product.FormaTorta.CIRCULAR, Product.TamañoProducto.PEQUEÑO, false, false);
        
        // Productos Sin Gluten
        crearProducto("PG001", "Brownie Sin Gluten",
                     "Rico y denso, este brownie es perfecto para quienes necesitan evitar el gluten sin sacrificar el sabor.",
                     new BigDecimal("4000"), Product.CategoriaProducto.PRODUCTOS_SIN_GLUTEN,
                     null, Product.TamañoProducto.INDIVIDUAL, false, false, false, true);
        
        crearProducto("PG002", "Pan Sin Gluten",
                     "Suave y esponjoso, ideal para sándwiches o para acompañar cualquier comida.",
                     new BigDecimal("3500"), Product.CategoriaProducto.PRODUCTOS_SIN_GLUTEN,
                     null, Product.TamañoProducto.PEQUEÑO, false, false, false, true);
        
        // Productos Veganos
        crearProducto("PV001", "Torta Vegana de Chocolate",
                     "Torta de chocolate húmeda y deliciosa, hecha sin productos de origen animal, perfecta para veganos.",
                     new BigDecimal("50000"), Product.CategoriaProducto.PRODUCTOS_VEGANA,
                     Product.FormaTorta.CIRCULAR, Product.TamañoProducto.MEDIANO, true, true, false, false, true);
        
        crearProducto("PV002", "Galletas Veganas de Avena",
                     "Crujientes y sabrosas, estas galletas son una excelente opción para un snack saludable y vegano.",
                     new BigDecimal("4500"), Product.CategoriaProducto.PRODUCTOS_VEGANA,
                     null, Product.TamañoProducto.PEQUEÑO, false, false, false, false, true);
        
        // Tortas Especiales
        crearProducto("TE001", "Torta Especial de Cumpleaños",
                     "Diseñada especialmente para celebraciones, personalizable con decoraciones y mensajes únicos.",
                     new BigDecimal("55000"), Product.CategoriaProducto.TORTAS_ESPECIALES,
                     Product.FormaTorta.CIRCULAR, Product.TamañoProducto.GRANDE, true, true);
        
    crearProducto("TE002", "Torta Especial de Boda",
             "Elegante y deliciosa, esta torta está diseñada para ser el centro de atención en cualquier boda.",
             new BigDecimal("60000"), Product.CategoriaProducto.TORTAS_ESPECIALES,
             Product.FormaTorta.CIRCULAR, Product.TamañoProducto.FAMILIAR, true, true);
        
        System.out.println("✅ Base de datos inicializada con " + productRepository.count() + " productos");
    }
    
    private void crearProducto(String codigo, String nombre, String descripcion, BigDecimal precio,
                              Product.CategoriaProducto categoria, Product.FormaTorta forma, 
                              Product.TamañoProducto tamaño, boolean personalizable, boolean mensajeEspecial) {
        crearProducto(codigo, nombre, descripcion, precio, categoria, forma, tamaño, personalizable, mensajeEspecial, false, false, false);
    }
    
    private void crearProducto(String codigo, String nombre, String descripcion, BigDecimal precio,
                              Product.CategoriaProducto categoria, Product.FormaTorta forma, 
                              Product.TamañoProducto tamaño, boolean personalizable, boolean mensajeEspecial,
                              boolean sinAzucar) {
        crearProducto(codigo, nombre, descripcion, precio, categoria, forma, tamaño, personalizable, mensajeEspecial, sinAzucar, false, false);
    }
    
    private void crearProducto(String codigo, String nombre, String descripcion, BigDecimal precio,
                              Product.CategoriaProducto categoria, Product.FormaTorta forma, 
                              Product.TamañoProducto tamaño, boolean personalizable, boolean mensajeEspecial,
                              boolean sinAzucar, boolean sinGluten) {
        crearProducto(codigo, nombre, descripcion, precio, categoria, forma, tamaño, personalizable, mensajeEspecial, sinAzucar, sinGluten, false);
    }
    
    private void crearProducto(String codigo, String nombre, String descripcion, BigDecimal precio,
                              Product.CategoriaProducto categoria, Product.FormaTorta forma, 
                              Product.TamañoProducto tamaño, boolean personalizable, boolean mensajeEspecial,
                              boolean sinAzucar, boolean sinGluten, boolean vegano) {
        
        Product product = new Product();
        product.setCodigo(codigo);
        product.setNombre(nombre);
        product.setDescripcion(descripcion);
        product.setPrecio(precio);
        product.setCategoria(categoria);
        product.setFormaTorta(forma);
        product.setTamaño(tamaño);
        product.setPersonalizable(personalizable);
        product.setMensajeEspecial(mensajeEspecial);
        product.setSinAzucar(sinAzucar);
        product.setSinGluten(sinGluten);
        product.setVegano(vegano);
        
        // Agregar historia/origen basado en la categoría
        switch (categoria) {
            case TORTAS_CUADRADAS, TORTAS_CIRCULARES -> product.setHistoriaOrigen("Receta tradicional familiar transmitida por generaciones de reposteros");
            case PASTELERIA_TRADICIONAL -> product.setHistoriaOrigen("Técnicas artesanales heredadas de la repostería europea clásica");
            case TORTAS_ESPECIALES -> product.setHistoriaOrigen("Creaciones únicas desarrolladas por estudiantes de gastronomía de Duoc");
            default -> product.setHistoriaOrigen("Producto elaborado con ingredientes selectos y técnicas modernas");
        }
        
        productRepository.save(product);
    }
}