package com.panaderia.api.controller;

import com.panaderia.api.entity.User;
import com.panaderia.api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody User user) {
        try {
            User usuarioRegistrado = authService.registrarUsuario(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario registrado exitosamente");
            response.put("user", Map.of(
                "id", usuarioRegistrado.getId(),
                "nombre", usuarioRegistrado.getNombre(),
                "email", usuarioRegistrado.getEmail(),
                "descuentoPorEdad", usuarioRegistrado.isDescuentoPorEdad(),
                "esEstudianteDuoc", usuarioRegistrado.isEsEstudianteDuoc()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");
            
            User user = authService.autenticarUsuario(email, password);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login exitoso");
            response.put("user", Map.of(
                "id", user.getId(),
                "nombre", user.getNombre(),
                "email", user.getEmail(),
                "descuentoPorEdad", user.isDescuentoPorEdad(),
                "esEstudianteDuoc", user.isEsEstudianteDuoc(),
                "codigoFelices50Usado", user.isCodigoFelices50Usado()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/{email}/codigo-descuento")
    public ResponseEntity<?> aplicarCodigoDescuento(
            @PathVariable String email, 
            @RequestBody Map<String, String> request) {
        try {
            String codigo = request.get("codigo");
            User user = authService.aplicarCodigoDescuento(email, codigo);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Código de descuento aplicado exitosamente");
            response.put("descuento", user.getPorcentajeDescuentoFelices50() + "%");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{email}/descuentos")
    public ResponseEntity<?> obtenerDescuentos(@PathVariable String email) {
        try {
            double descuentoTotal = authService.calcularDescuentoTotal(email);
            boolean tortaGratis = authService.puedeAccederTortaGratis(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("descuentoTotal", descuentoTotal + "%");
            response.put("tortaGratisCumpleanos", tortaGratis);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener descuentos");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping
    public ResponseEntity<?> listarUsuarios() {
        try {
            // Obtener todos los usuarios (solo para testing)
            java.util.List<User> usuarios = authService.obtenerTodosLosUsuarios();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", usuarios.size());
            response.put("usuarios", usuarios.stream().map(user -> Map.of(
                "id", user.getId(),
                "nombre", user.getNombre(),
                "apellido", user.getApellido(),
                "email", user.getEmail(),
                "telefono", user.getTelefono() != null ? user.getTelefono() : "",
                "fechaNacimiento", user.getFechaNacimiento(),
                "direccion", user.getDireccion() != null ? user.getDireccion() : "",
                "descuentoPorEdad", user.isDescuentoPorEdad(),
                "esEstudianteDuoc", user.isEsEstudianteDuoc(),
                "fechaRegistro", user.getFechaRegistro()
            )).toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener usuarios: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Sistema de usuarios funcionando correctamente");
        response.put("endpoints", Map.of(
            "listar", "GET /api/usuarios",
            "registro", "POST /api/usuarios/registro",
            "login", "POST /api/usuarios/login",
            "codigoDescuento", "POST /api/usuarios/{email}/codigo-descuento",
            "descuentos", "GET /api/usuarios/{email}/descuentos"
        ));
        
        return ResponseEntity.ok(response);
    }
}