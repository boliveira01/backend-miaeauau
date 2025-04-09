package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "funcionarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    // Podemos adicionar informações sobre cargo, data de admissão, etc.
    private String cargo;

    // Informações de login (poderíamos criar uma entidade separada para segurança no futuro)
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    // Indica se o funcionário é um administrador (terá mais permissões)
    private boolean administrador;

    // Podemos adicionar mais campos relevantes para o funcionário no futuro
}