package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O CRMV é obrigatório")
    @Column(nullable = false, unique = true)
    private String crmv;

    @NotBlank(message = "A especialidade é obrigatória")
    @Size(max = 255, message = "A especialidade não pode ter mais de 255 caracteres")
    private String especialidade;

    @OneToMany(mappedBy = "veterinario")
    private List<Consulta> consultas;

    @OneToMany(mappedBy = "veterinario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DisponibilidadeVeterinario> disponibilidades;
}