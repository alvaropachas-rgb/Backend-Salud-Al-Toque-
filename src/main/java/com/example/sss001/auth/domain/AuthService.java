package com.example.sss001.auth.domain;

import com.example.sss001.auth.components.JwtService;
import com.example.sss001.auth.dto.SignInRequest;
import com.example.sss001.auth.dto.SignUpRequest;
import com.example.sss001.auth.dto.TokenResponse;

import com.example.sss001.patient.domain.Patient;
import com.example.sss001.patient.infrastructure.PatientRepository;

import com.example.sss001.user.domain.User;
import com.example.sss001.user.infrastructure.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================================================
    // SIGN IN
    // =========================================================

    public TokenResponse signIn(
            SignInRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        String token =
                jwtService.generateToken(
                        request.getEmail()
                );

        return new TokenResponse(token);
    }

    // =========================================================
    // SIGN UP
    // =========================================================

    public TokenResponse signUp(
            SignUpRequest request) {

        if (userRepository.findByEmail(
                request.getEmail()
        ).isPresent()) {

            throw new IllegalArgumentException(
                    "El correo electrónico ya está registrado"
            );
        }

        // -------------------------------------------------
        // CREAR USER
        // -------------------------------------------------

        User user =
                new User();

        user.setName(
                request.getName()
        );

        user.setEmail(
                request.getEmail()
        );

        // IMPORTANTE:
        // Nunca guardar password directamente.
        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setPhone(
                request.getPhone()
        );

        user.setRole(
                "PATIENT"
        );

        User savedUser =
                userRepository.save(user);

        // -------------------------------------------------
        // CREAR PATIENT
        // -------------------------------------------------

        Patient patient =
                new Patient();

        patient.setUser(
                savedUser
        );

        patientRepository.save(patient);

        // -------------------------------------------------
        // GENERAR JWT
        // -------------------------------------------------

        String token =
                jwtService.generateToken(
                        savedUser.getEmail()
                );

        return new TokenResponse(token);
    }
}