package com.miaueauau.clinica_veterinaria.controller;


import com.miaueauau.clinica_veterinaria.model.Funcionario;
import com.miaueauau.clinica_veterinaria.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// DTO simples para a requisição de login
// Você pode criar uma classe separada para isso (ex: LoginRequest.java)
// Mas para simplicidade, vamos usar um Map aqui.
/*
public class LoginRequest {
    private String email;
    private String senha;
    // Getters e Setters
}
*/

@RestController
@RequestMapping("/api/login") // Endpoint para o login
public class LoginController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @PostMapping
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String senha = loginData.get("senha");

        // 1. Busca o funcionário pelo e-mail no banco de dados
        Optional<Funcionario> funcionarioOptional = funcionarioRepository.findByEmail(email);

        // 2. Verifica se o funcionário existe E se a senha confere
        if (funcionarioOptional.isPresent()) {
            Funcionario funcionario = funcionarioOptional.get();
            // Comparação de senha em texto puro (APENAS PARA HOMOLOGAÇÃO/TESTE BÁSICO)
            if (funcionario.getSenha().equals(senha)) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Login bem-sucedido!");
                response.put("nome", funcionario.getNome());
                response.put("email", funcionario.getEmail());
                // Se tiver o campo administrador:
                response.put("administrador", String.valueOf(funcionario.isAdministrador()));
                // Sem JWT, não há token para retornar, apenas a mensagem de sucesso
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }

        // Se o login falhou
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "Credenciais inválidas.");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED); // 401 Unauthorized
    }
}