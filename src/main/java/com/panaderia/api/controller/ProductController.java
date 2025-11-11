package com.panaderia.api.controller;

import com.panaderia.api.entity.Product;
import com.panaderia.api.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

/**
 * Controlador REST para el catálogo de productos de panadería
 * Incluye filtros avanzados y búsqueda por múltiples criterios
 */
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private ProductRepository productRepository;
    
    /**
     * Obtiene todos los productos activos
     * GET /api/productos
     */
    @GetMapping
    public ResponseEntity<List<Product>> obtenerTodosLosProductos() {
        List<Product> productos = productRepository.findByActivoTrue();
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Obtiene todas las categorías disponibles
     * GET /api/productos/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> obtenerCategorias() {
        List<String> categorias = productRepository.findDistinctCategorias();
        return ResponseEntity.ok(categorias);
    }
    
    /**
     * Obtiene un producto por ID
     * GET /api/productos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> obtenerProductoPorId(@PathVariable Long id) {
        Optional<Product> producto = productRepository.findById(id);
        return producto.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Obtiene producto por código
     * GET /api/productos/codigo/{codigo}
     */
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Product> obtenerProductoPorCodigo(@PathVariable String codigo) {
        Optional<Product> producto = productRepository.findByCodigoAndActivoTrue(codigo);
        return producto.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Obtiene productos por categoría
     * GET /api/productos/categoria/{categoria}
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Product>> obtenerProductosPorCategoria(@PathVariable String categoria) {
        try {
            Product.CategoriaProducto cat = Product.CategoriaProducto.valueOf(categoria.toUpperCase());
            List<Product> productos = productRepository.findByCategoriaAndActivoTrue(cat);
            return ResponseEntity.ok(productos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtiene tortas por forma (cuadrada o circular)
     * GET /api/productos/forma/{forma}
     */
    @GetMapping("/forma/{forma}")
    public ResponseEntity<List<Product>> obtenerTortasPorForma(@PathVariable String forma) {
        try {
            Product.FormaTorta f = Product.FormaTorta.valueOf(forma.toUpperCase());
            List<Product> productos = productRepository.findByFormaTortaAndActivoTrue(f);
            return ResponseEntity.ok(productos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtiene productos por tamaño
     * GET /api/productos/tamaño/{tamaño}
     */
    @GetMapping("/tamaño/{tamaño}")
    public ResponseEntity<List<Product>> obtenerProductosPorTamaño(@PathVariable String tamaño) {
        try {
            Product.TamañoProducto t = Product.TamañoProducto.valueOf(tamaño.toUpperCase());
            List<Product> productos = productRepository.findByTamañoAndActivoTrue(t);
            return ResponseEntity.ok(productos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Obtiene productos personalizables
     * GET /api/productos/personalizables
     */
    @GetMapping("/personalizables")
    public ResponseEntity<List<Product>> obtenerProductosPersonalizables() {
        List<Product> productos = productRepository.findByPersonalizableAndActivoTrue(true);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Obtiene productos sin azúcar
     * GET /api/productos/sin-azucar
     */
    @GetMapping("/sin-azucar")
    public ResponseEntity<List<Product>> obtenerProductosSinAzucar() {
        List<Product> productos = productRepository.findBySinAzucarAndActivoTrue(true);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Obtiene productos sin gluten
     * GET /api/productos/sin-gluten
     */
    @GetMapping("/sin-gluten")
    public ResponseEntity<List<Product>> obtenerProductosSinGluten() {
        List<Product> productos = productRepository.findBySinGlutenAndActivoTrue(true);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Obtiene productos veganos
     * GET /api/productos/veganos
     */
    @GetMapping("/veganos")
    public ResponseEntity<List<Product>> obtenerProductosVeganos() {
        List<Product> productos = productRepository.findByVeganoAndActivoTrue(true);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Busca productos por nombre
     * GET /api/productos/buscar?q={texto}
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Product>> buscarProductosPorNombre(@RequestParam String q) {
        List<Product> productos = productRepository.findByNombreContainingIgnoreCaseAndActivoTrue(q);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Búsqueda avanzada con múltiples filtros
     * GET /api/productos/busqueda-avanzada?categoria=...&forma=...&tamaño=...
     */
    @GetMapping("/busqueda-avanzada")
    public ResponseEntity<List<Product>> busquedaAvanzada(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String forma,
            @RequestParam(required = false) String tamaño,
            @RequestParam(required = false) Boolean sinAzucar,
            @RequestParam(required = false) Boolean sinGluten,
            @RequestParam(required = false) Boolean vegano,
            @RequestParam(required = false) Boolean personalizable) {
        
        Product.CategoriaProducto cat = null;
        Product.FormaTorta f = null;
        Product.TamañoProducto t = null;
        
        try {
            if (categoria != null) cat = Product.CategoriaProducto.valueOf(categoria.toUpperCase());
            if (forma != null) f = Product.FormaTorta.valueOf(forma.toUpperCase());
            if (tamaño != null) t = Product.TamañoProducto.valueOf(tamaño.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Product> productos = productRepository.busquedaAvanzada(cat, f, t, sinAzucar, sinGluten, vegano, personalizable);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Obtiene productos en un rango de precios
     * GET /api/productos/precio?min={min}&max={max}
     */
    @GetMapping("/precio")
    public ResponseEntity<List<Product>> obtenerProductosPorPrecio(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        List<Product> productos = productRepository.findByPrecioBetweenAndActivoTrue(min, max);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Obtiene productos recomendados
     * GET /api/productos/recomendados
     */
    @GetMapping("/recomendados")
    public ResponseEntity<List<Product>> obtenerProductosRecomendados() {
        List<Product> productos = productRepository.findProductosRecomendados();
        // Limitar a los primeros 10
        if (productos.size() > 10) {
            productos = productos.subList(0, 10);
        }
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Crea un nuevo producto
     * POST /api/productos
     */
    @PostMapping
    public ResponseEntity<?> crearProducto(@RequestBody Product producto) {
        try {
            // Verificar si el código ya existe
            if (productRepository.findByCodigoAndActivoTrue(producto.getCodigo()).isPresent()) {
                return ResponseEntity.badRequest().body(
                    java.util.Map.of("error", "Ya existe un producto con el código: " + producto.getCodigo())
                );
            }
            
            // Establecer como activo por defecto
            producto.setActivo(true);
            
            // Establecer fecha de creación
            producto.setFechaCreacion(java.time.LocalDateTime.now());
            
            Product productoGuardado = productRepository.save(producto);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(
                java.util.Map.of(
                    "success", true,
                    "message", "Producto creado exitosamente",
                    "producto", productoGuardado
                )
            );
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                java.util.Map.of("error", "Error al crear producto: " + e.getMessage())
            );
        }
    }

    /**
     * Actualizar producto existente
     * PUT /api/productos/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProducto(@PathVariable Long id, @RequestBody Product productoActualizado) {
        try {
            Optional<Product> productoOpt = productRepository.findById(id);
            if (!productoOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    java.util.Map.of("success", false, "message", "Producto no encontrado")
                );
            }
            
            Product productoExistente = productoOpt.get();
            
            // Actualizar campos
            productoExistente.setNombre(productoActualizado.getNombre());
            productoExistente.setDescripcion(productoActualizado.getDescripcion());
            productoExistente.setPrecio(productoActualizado.getPrecio());
            productoExistente.setCategoria(productoActualizado.getCategoria());
            productoExistente.setFormaTorta(productoActualizado.getFormaTorta());
            productoExistente.setTamaño(productoActualizado.getTamaño());
            productoExistente.setPersonalizable(productoActualizado.getPersonalizable());
            productoExistente.setMensajeEspecial(productoActualizado.getMensajeEspecial());
            productoExistente.setSinAzucar(productoActualizado.getSinAzucar());
            productoExistente.setSinGluten(productoActualizado.getSinGluten());
            productoExistente.setVegano(productoActualizado.getVegano());
            productoExistente.setHistoriaOrigen(productoActualizado.getHistoriaOrigen());
            
            if (productoActualizado.getStock() != null) {
                productoExistente.setStock(productoActualizado.getStock());
            }
            if (productoActualizado.getStockMinimo() != null) {
                productoExistente.setStockMinimo(productoActualizado.getStockMinimo());
            }
            if (productoActualizado.getActivo() != null) {
                productoExistente.setActivo(productoActualizado.getActivo());
            }
            
            Product productoGuardado = productRepository.save(productoExistente);
            
            return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "message", "Producto actualizado exitosamente",
                "producto", productoGuardado
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                java.util.Map.of("success", false, "message", "Error al actualizar producto: " + e.getMessage())
            );
        }
    }

    /**
     * Eliminar producto (desactivar)
     * DELETE /api/productos/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            Optional<Product> productoOpt = productRepository.findById(id);
            if (!productoOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    java.util.Map.of("success", false, "message", "Producto no encontrado")
                );
            }
            
            Product producto = productoOpt.get();
            producto.setActivo(false); // Soft delete
            productRepository.save(producto);
            
            return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "message", "Producto eliminado exitosamente"
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                java.util.Map.of("success", false, "message", "Error al eliminar producto: " + e.getMessage())
            );
        }
    }

    /**
     * Consultar stock de un producto
     * GET /api/productos/{id}/stock
     */
    @GetMapping("/{id}/stock")
    public ResponseEntity<?> consultarStock(@PathVariable Long id) {
        try {
            Optional<Product> productoOpt = productRepository.findById(id);
            if (!productoOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    java.util.Map.of("success", false, "message", "Producto no encontrado")
                );
            }
            
            Product producto = productoOpt.get();
            
            return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "producto", producto.getNombre(),
                "stock", producto.getStock(),
                "stockMinimo", producto.getStockMinimo(),
                "tieneStock", producto.tieneStock(),
                "stockBajo", producto.stockBajo()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                java.util.Map.of("success", false, "message", "Error al consultar stock: " + e.getMessage())
            );
        }
    }

    /**
     * Actualizar stock de un producto
     * PUT /api/productos/{id}/stock
     */
    @PutMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> stockData) {
        try {
            Optional<Product> productoOpt = productRepository.findById(id);
            if (!productoOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    java.util.Map.of("success", false, "message", "Producto no encontrado")
                );
            }
            
            Product producto = productoOpt.get();
            Integer nuevoStock = stockData.get("stock");
            Integer stockMinimo = stockData.get("stockMinimo");
            
            if (nuevoStock != null) {
                producto.setStock(nuevoStock);
            }
            if (stockMinimo != null) {
                producto.setStockMinimo(stockMinimo);
            }
            
            productRepository.save(producto);
            
            return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "message", "Stock actualizado exitosamente",
                "stock", producto.getStock(),
                "stockMinimo", producto.getStockMinimo(),
                "stockBajo", producto.stockBajo()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                java.util.Map.of("success", false, "message", "Error al actualizar stock: " + e.getMessage())
            );
        }
    }

    /**
     * Agregar stock a un producto
     * POST /api/productos/{id}/stock
     */
    @PostMapping("/{id}/stock")
    public ResponseEntity<?> agregarStock(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> stockData) {
        try {
            Optional<Product> productoOpt = productRepository.findById(id);
            if (!productoOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    java.util.Map.of("success", false, "message", "Producto no encontrado")
                );
            }
            
            Product producto = productoOpt.get();
            Integer cantidadAgregar = stockData.get("cantidad");
            
            if (cantidadAgregar == null || cantidadAgregar < 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    java.util.Map.of("success", false, "message", "Cantidad debe ser mayor a 0")
                );
            }
            
            producto.aumentarStock(cantidadAgregar);
            productRepository.save(producto);
            
            return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "message", "Stock agregado exitosamente",
                "stockAnterior", producto.getStock() - cantidadAgregar,
                "cantidadAgregada", cantidadAgregar,
                "stockActual", producto.getStock()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                java.util.Map.of("success", false, "message", "Error al agregar stock: " + e.getMessage())
            );
        }
    }

    /**
     * Reporte de inventario completo
     * GET /api/productos/inventario
     */
    @GetMapping("/inventario")
    public ResponseEntity<?> reporteInventario() {
        try {
            List<Product> productos = productRepository.findAll();
            
            // Clasificar productos por estado de stock
            List<Product> stockBajo = productos.stream()
                .filter(p -> p.getActivo() && p.stockBajo())
                .toList();
            
            List<Product> sinStock = productos.stream()
                .filter(p -> p.getActivo() && !p.tieneStock())
                .toList();
            
            List<Product> stockNormal = productos.stream()
                .filter(p -> p.getActivo() && p.tieneStock() && !p.stockBajo())
                .toList();
            
            // Calcular métricas
            int totalProductos = productos.size();
            int productosActivos = (int) productos.stream().filter(Product::getActivo).count();
            int productosInactivos = totalProductos - productosActivos;
            
            java.math.BigDecimal valorInventario = productos.stream()
                .filter(Product::getActivo)
                .filter(p -> p.getStock() != null && p.getPrecio() != null)
                .map(p -> p.getPrecio().multiply(java.math.BigDecimal.valueOf(p.getStock())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            
            Map<String, Object> inventario = new HashMap<>();
            inventario.put("resumen", Map.of(
                "totalProductos", totalProductos,
                "productosActivos", productosActivos,
                "productosInactivos", productosInactivos,
                "valorTotalInventario", valorInventario,
                "alertasStockBajo", stockBajo.size(),
                "productosSinStock", sinStock.size()
            ));
            
            inventario.put("alertas", Map.of(
                "stockBajo", stockBajo.stream().map(p -> Map.of(
                    "id", p.getId(),
                    "nombre", p.getNombre(),
                    "stock", p.getStock(),
                    "stockMinimo", p.getStockMinimo(),
                    "valorUnitario", p.getPrecio()
                )).toList(),
                "sinStock", sinStock.stream().map(p -> Map.of(
                    "id", p.getId(),
                    "nombre", p.getNombre(),
                    "valorUnitario", p.getPrecio()
                )).toList()
            ));
            
            inventario.put("productos", productos.stream().map(p -> Map.of(
                "id", p.getId(),
                "codigo", p.getCodigo(),
                "nombre", p.getNombre(),
                "categoria", p.getCategoria(),
                "precio", p.getPrecio(),
                "stock", p.getStock() != null ? p.getStock() : 0,
                "stockMinimo", p.getStockMinimo() != null ? p.getStockMinimo() : 0,
                "valorInventario", p.getPrecio() != null && p.getStock() != null ? 
                    p.getPrecio().multiply(java.math.BigDecimal.valueOf(p.getStock())) : java.math.BigDecimal.ZERO,
                "estado", p.getActivo() ? "ACTIVO" : "INACTIVO",
                "estadoStock", !p.tieneStock() ? "SIN_STOCK" : 
                               p.stockBajo() ? "STOCK_BAJO" : "STOCK_NORMAL"
            )).toList());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "inventario", inventario
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                java.util.Map.of("success", false, "message", "Error al generar reporte de inventario: " + e.getMessage())
            );
        }
    }
    
    /**
     * Productos más vendidos (requiere integración con ventas)
     * GET /api/productos/mas-vendidos
     */
    @GetMapping("/mas-vendidos")
    public ResponseEntity<?> productosMasVendidos() {
        try {
            // Esta funcionalidad estaría mejor en VentaService, pero la implementamos aquí para completitud
            List<Product> productos = productRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Para estadísticas detalladas de productos más vendidos, usar: GET /api/ventas/estadisticas");
            response.put("productosDisponibles", productos.stream()
                .filter(Product::getActivo)
                .filter(Product::tieneStock)
                .map(p -> Map.of(
                    "id", p.getId(),
                    "nombre", p.getNombre(),
                    "categoria", p.getCategoria(),
                    "precio", p.getPrecio(),
                    "stock", p.getStock()
                ))
                .toList()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                java.util.Map.of("success", false, "message", "Error al obtener productos más vendidos: " + e.getMessage())
            );
        }
    }

    /**
     * Endpoint de prueba
     * GET /api/productos/test
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("🍰 API de Panadería funcionando correctamente!");
    }
}