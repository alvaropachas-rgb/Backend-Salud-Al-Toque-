package com.example.sss001.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

// Convierte las excepciones comunes en respuestas JSON claras
// en vez de dejar que Spring devuelva un error 500 genérico.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Respaldo final contra la doble reserva: si dos peticiones
    // pasan la validación al mismo tiempo exacto (condición de
    // carrera), la restricción única de la base de datos rechaza
    // la segunda, y la convertimos en un 409 claro.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrity(
            DataIntegrityViolationException ex) {

        return buildResponse(
                HttpStatus.CONFLICT,
                "Ese horario ya no está disponible para este profesional"
        );
    }

    // Ej: correo ya registrado en el signup.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(
            IllegalArgumentException ex) {

        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Ej: email/contraseña incorrectos en el signin.
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(
            BadCredentialsException ex) {

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Correo o contraseña incorrectos"
        );
    }

    // Errores lanzados explícitamente con ResponseStatusException
    // (ej. "Paciente no encontrado" en AppointmentController).
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatus(
            ResponseStatusException ex) {

        return buildResponse(
                HttpStatus.valueOf(ex.getStatusCode().value()),
                ex.getReason()
        );
    }

    // Red de seguridad: cualquier otro error no controlado.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneric(Exception ex) {

        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado"
        );
    }

    private ResponseEntity<Object> buildResponse(
            HttpStatus status, String message) {

        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }
}