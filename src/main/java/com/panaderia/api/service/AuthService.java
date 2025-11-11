package com.panaderia.api.service;

import com.panaderia.api.entity.User;
import com.panaderia.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registrarUsuario(User user) throws Exception {
        // Verificar si el email ya existe
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new Exception("El email ya está registrado");
        }

        // Encriptar contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Verificar descuento por edad (50+ años)
        if (user.getFechaNacimiento() != null) {
            LocalDate hoy = LocalDate.now();
            int edad = Period.between(user.getFechaNacimiento(), hoy).getYears();
            
            if (edad >= 50) {
                user.setDescuentoPorEdad(true);
                user.setPorcentajeDescuentoEdad(50);
            }
        }

        // Verificar si es estudiante Duoc
        if (user.getEmail().endsWith("@duoc.cl")) {
            user.setEsEstudianteDuoc(true);
        }

        return userRepository.save(user);
    }

    public User aplicarCodigoDescuento(String email, String codigo) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            throw new Exception("Usuario no encontrado");
        }

        User user = userOpt.get();

        if ("FELICES50".equals(codigo)) {
            if (user.isCodigoFelices50Usado()) {
                throw new Exception("Este código ya fue utilizado");
            }
            
            user.setCodigoFelices50Usado(true);
            user.setPorcentajeDescuentoFelices50(10);
            user = userRepository.save(user);
            
            return user;
        } else {
            throw new Exception("Código de descuento inválido");
        }
    }

    public User autenticarUsuario(String email, String password) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            throw new Exception("Usuario no encontrado");
        }

        User user = userOpt.get();
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Contraseña incorrecta");
        }

        return user;
    }

    public boolean puedeAccederTortaGratis(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent() || !userOpt.get().isEsEstudianteDuoc()) {
            return false;
        }

        User user = userOpt.get();
        LocalDate hoy = LocalDate.now();
        LocalDate cumpleanos = user.getFechaNacimiento();

        if (cumpleanos == null) {
            return false;
        }

        // Verificar si hoy es su cumpleaños (mes y día)
        return cumpleanos.getMonth() == hoy.getMonth() && 
               cumpleanos.getDayOfMonth() == hoy.getDayOfMonth();
    }

    public double calcularDescuentoTotal(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (!userOpt.isPresent()) {
            return 0.0;
        }

        User user = userOpt.get();
        double descuentoTotal = 0.0;

        // Descuento por edad (50%)
        if (user.isDescuentoPorEdad()) {
            descuentoTotal += user.getPorcentajeDescuentoEdad();
        }

        // Descuento FELICES50 (10%)
        if (user.isCodigoFelices50Usado()) {
            descuentoTotal += user.getPorcentajeDescuentoFelices50();
        }

        // Los descuentos no se acumulan, se toma el mayor
        return Math.max(
            user.isDescuentoPorEdad() ? user.getPorcentajeDescuentoEdad() : 0,
            user.isCodigoFelices50Usado() ? user.getPorcentajeDescuentoFelices50() : 0
        );
    }
    
    public java.util.List<User> obtenerTodosLosUsuarios() {
        return userRepository.findAll();
    }
}