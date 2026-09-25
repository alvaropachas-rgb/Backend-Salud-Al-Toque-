package com.example.sss001.user.application;

import com.example.sss001.exceptions.ResourceNotFoundException;
import com.example.sss001.user.domain.User;
import com.example.sss001.user.domain.UserService;
import com.example.sss001.user.dto.UserDTO;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;
    private final PasswordEncoder passwordEncoder;

    public UserController(
            UserService service,
            PasswordEncoder passwordEncoder) {

        this.service = service;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> findAll() {

        return service.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO findById(
            @PathVariable Long id) {

        User user =
                service.findById(id);

        if (user == null) {
            throw new ResourceNotFoundException(
                    "Usuario no encontrado"
            );
        }

        return convertToDTO(user);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO save(
            @RequestBody User user) {

        User saved =
                service.save(user);

        return convertToDTO(saved);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(
            @PathVariable Long id) {

        User user =
                service.findById(id);

        if (user == null) {
            throw new ResourceNotFoundException(
                    "Usuario no encontrado"
            );
        }

        service.delete(id);
    }

    private UserDTO convertToDTO(
            User user) {

        UserDTO dto =
                new UserDTO();

        dto.setId(
                user.getId()
        );

        dto.setName(
                user.getName()
        );

        dto.setEmail(
                user.getEmail()
        );

        dto.setPhone(
                user.getPhone()
        );

        dto.setRole(
                user.getRole()
        );

        return dto;
    }
}