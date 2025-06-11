package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
@Table(name = "disponibilidades_veterinarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadeVeterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FetchType.EAGER para Veterinario
    @ManyToOne(fetch = FetchType.EAGER) // <<< MUDANÇA AQUI
    @JoinColumn(name = "veterinario_id", nullable = false)
    @JsonIgnoreProperties({"consultas", "disponibilidades"}) // Ignora as listas do Veterinario
    private Veterinario veterinario;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column(nullable = false)
    private LocalDateTime fim;

    private String observacoes;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DiaSemana diaSemana;
}