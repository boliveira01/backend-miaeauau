package com.miaueauau.clinica_veterinaria.controller;

import com.miaueauau.clinica_veterinaria.dto.LoginRequest;
import com.miaueauau.clinica_veterinaria.dto.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(new LoginResponse(null, "Credenciais inválidas"));
        }

        // Se a autenticação for bem-sucedida, retorne um token de teste
        String dummyToken = "homologation-test-token-123";
        return ResponseEntity.ok(new LoginResponse(dummyToken, "Login realizado com sucesso para homologação!"));
    }
}