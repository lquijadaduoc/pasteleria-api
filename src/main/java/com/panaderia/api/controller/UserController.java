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
            response.put("usuarios", usuarios.stream().map(user -> {
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("nombre", user.getNombre());
                userData.put("apellido", user.getApellido());
                userData.put("email", user.getEmail());
                userData.put("telefono", user.getTelefono() != null ? user.getTelefono() : "");
                userData.put("fechaNacimiento", user.getFechaNacimiento());
                userData.put("direccion", user.getDireccion() != null ? user.getDireccion() : "");
                userData.put("rol", user.getRol().toString());
                userData.put("descuentoPorEdad", user.isDescuentoPorEdad());
                userData.put("esEstudianteDuoc", user.isEsEstudianteDuoc());
                userData.put("fechaRegistro", user.getFechaRegistro());
                return userData;
            }).toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener usuarios: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Cambiar rol de usuario (solo para administradores)
     * PUT /api/usuarios/{id}/rol
     */
    @PutMapping("/{id}/rol")
    public ResponseEntity<?> cambiarRolUsuario(@PathVariable Long id, @RequestBody Map<String, String> datos) {
        try {
            String nuevoRol = datos.get("rol");
            
            if (nuevoRol == null || nuevoRol.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "El rol es obligatorio"
                ));
            }
            
            // Validar que el rol sea válido
            try {
                User.RolUsuario.valueOf(nuevoRol.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Rol inválido. Roles válidos: CLIENTE, ADMIN, EMPLEADO"
                ));
            }
            
            User usuario = authService.cambiarRolUsuario(id, nuevoRol);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Rol actualizado exitosamente");
            response.put("usuario", Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "email", usuario.getEmail(),
                "rol", usuario.getRol().toString()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error al cambiar rol: " + e.getMessage()
            ));
        }
    }

    /**
     * Obtener usuario por ID
     * GET /api/usuarios/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Long id) {
        try {
            User usuario = authService.obtenerUsuarioPorId(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            
            Map<String, Object> usuarioData = new HashMap<>();
            usuarioData.put("id", usuario.getId());
            usuarioData.put("nombre", usuario.getNombre());
            usuarioData.put("apellido", usuario.getApellido());
            usuarioData.put("email", usuario.getEmail());
            usuarioData.put("telefono", usuario.getTelefono() != null ? usuario.getTelefono() : "");
            usuarioData.put("fechaNacimiento", usuario.getFechaNacimiento());
            usuarioData.put("direccion", usuario.getDireccion() != null ? usuario.getDireccion() : "");
            usuarioData.put("rol", usuario.getRol().toString());
            usuarioData.put("descuentoPorEdad", usuario.isDescuentoPorEdad());
            usuarioData.put("esEstudianteDuoc", usuario.isEsEstudianteDuoc());
            usuarioData.put("fechaRegistro", usuario.getFechaRegistro());
            
            response.put("usuario", usuarioData);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                "success", false,
                "message", "Usuario no encontrado: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Sistema de usuarios funcionando correctamente");
        response.put("endpoints", Map.of(
            "listar", "GET /api/usuarios",
            "obtener", "GET /api/usuarios/{id}",
            "registro", "POST /api/usuarios/registro",
            "login", "POST /api/usuarios/login",
            "cambiarRol", "PUT /api/usuarios/{id}/rol",
            "codigoDescuento", "POST /api/usuarios/{email}/codigo-descuento",
            "descuentos", "GET /api/usuarios/{email}/descuentos"
        ));
        
        return ResponseEntity.ok(response);
    }
}