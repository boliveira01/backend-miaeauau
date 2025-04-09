package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "veterinarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String crmv; // Conselho Regional de Medicina Veterinária

    private String especialidade;

    // Podemos adicionar informações sobre horários de atendimento aqui no futuro
    // ou criar uma entidade separada para isso, dependendo da complexidade.

    @OneToMany(mappedBy = "veterinario")
    private List<Consulta> consultas;

    // Podemos adicionar mais campos relevantes para o veterinário no futuro
}