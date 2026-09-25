package com.example.sss001.auth.domain;

import com.example.sss001.auth.components.JwtService;
import com.example.sss001.auth.dto.SignInRequest;
import com.example.sss001.auth.dto.SignUpRequest;
import com.example.sss001.auth.dto.TokenResponse;
import com.example.sss001.patient.domain.Patient;
import com.example.sss001.patient.infrastructure.PatientRepository;
import com.example.sss001.user.domain.CustomUserDetailsService;
import com.example.sss001.user.domain.User;
import com.example.sss001.user.infrastructure.UserRepository;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            JwtService jwtService,
            UserRepository userRepository,
            PatientRepository patientRepository,
            PasswordEncoder passwordEncoder) {

        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // LOGIN


    public TokenResponse signIn(SignInRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String token =
                jwtService.generateToken(userDetails);

        return new TokenResponse(token);
    }


    // REGISTRO


    public User signUp(SignUpRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {

            throw new IllegalArgumentException(
                    "El correo ya está registrado"
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setPhone(request.getPhone());

        // Todo registro público comienza como paciente
        user.setRole("PATIENT");

        User savedUser = userRepository.save(user);

        // Se crea automáticamente el perfil de Patient asociado,
        // para que el usuario pueda pedir citas de inmediato.
        Patient patient = new Patient();
        patient.setUser(savedUser);
        patientRepository.save(patient);

        return savedUser;
    }
}