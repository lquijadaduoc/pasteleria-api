package com.panaderia.api.repository;

import com.panaderia.api.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Product
 * Incluye consultas específicas para el catálogo de panadería
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * Encuentra productos activos
     */
    List<Product> findByActivoTrue();
    
    /**
     * Encuentra productos por categoría
     */
    List<Product> findByCategoriaAndActivoTrue(Product.CategoriaProducto categoria);
    
    /**
     * Encuentra productos por forma de torta
     */
    List<Product> findByFormaTortaAndActivoTrue(Product.FormaTorta forma);
    
    /**
     * Encuentra productos por tamaño
     */
    List<Product> findByTamañoAndActivoTrue(Product.TamañoProducto tamaño);
    
    /**
     * Encuentra productos personalizables
     */
    List<Product> findByPersonalizableAndActivoTrue(Boolean personalizable);
    
    /**
     * Encuentra productos sin azúcar
     */
    List<Product> findBySinAzucarAndActivoTrue(Boolean sinAzucar);
    
    /**
     * Encuentra productos sin gluten
     */
    List<Product> findBySinGlutenAndActivoTrue(Boolean sinGluten);
    
    /**
     * Encuentra productos veganos
     */
    List<Product> findByVeganoAndActivoTrue(Boolean vegano);
    
    /**
     * Encuentra productos por código
     */
    Optional<Product> findByCodigoAndActivoTrue(String codigo);
    
    /**
     * Busca productos por nombre (búsqueda parcial)
     */
    List<Product> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
    
    /**
     * Encuentra productos en un rango de precios
     */
    List<Product> findByPrecioBetweenAndActivoTrue(BigDecimal precioMin, BigDecimal precioMax);
    
    /**
     * Búsqueda avanzada con filtros múltiples
     */
    @Query("SELECT p FROM Product p WHERE p.activo = true " +
           "AND (:categoria IS NULL OR p.categoria = :categoria) " +
           "AND (:forma IS NULL OR p.formaTorta = :forma) " +
           "AND (:tamaño IS NULL OR p.tamaño = :tamaño) " +
           "AND (:sinAzucar IS NULL OR p.sinAzucar = :sinAzucar) " +
           "AND (:sinGluten IS NULL OR p.sinGluten = :sinGluten) " +
           "AND (:vegano IS NULL OR p.vegano = :vegano) " +
           "AND (:personalizable IS NULL OR p.personalizable = :personalizable)")
    List<Product> busquedaAvanzada(@Param("categoria") Product.CategoriaProducto categoria,
                                  @Param("forma") Product.FormaTorta forma,
                                  @Param("tamaño") Product.TamañoProducto tamaño,
                                  @Param("sinAzucar") Boolean sinAzucar,
                                  @Param("sinGluten") Boolean sinGluten,
                                  @Param("vegano") Boolean vegano,
                                  @Param("personalizable") Boolean personalizable);
    
    /**
     * Obtiene productos recomendados (más vendidos)
     */
    @Query("SELECT p FROM Product p WHERE p.activo = true ORDER BY p.fechaCreacion DESC")
    List<Product> findProductosRecomendados();
    
    /**
     * Obtiene todas las categorías distintas
     */
    @Query("SELECT DISTINCT p.categoria FROM Product p WHERE p.activo = true")
    List<String> findDistinctCategorias();
    
    /**
     * Verifica si existe un producto con el código dado
     */
    boolean existsByCodigoAndActivoTrue(String codigo);
}