// src/main/java/com/miaueauau/clinica_veterinaria/dto/LoginResponse.java
package com.miaueauau.clinica_veterinaria.dto;

import lombok.AllArgsConstructor; // Certifique-se de que o Lombok está no seu pom.xml
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Anotação do Lombok para gerar getters, setters, toString, etc.
@NoArgsConstructor // Construtor sem argumentos
@AllArgsConstructor // Construtor com todos os argumentos
public class LoginResponse {
    private String token;
    private String message;
}