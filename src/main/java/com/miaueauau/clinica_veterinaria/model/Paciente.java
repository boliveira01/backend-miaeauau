package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List; // Importar java.util.List
import com.fasterxml.jackson.annotation.JsonManagedReference; // JÁ ESTÁ NO Paciente.java
import com.fasterxml.jackson.annotation.JsonBackReference; // <-- NOVO IMPORT

@Entity
@Table(name = "pacientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String especie;

    @Column(nullable = false)
    private String raca;

    private LocalDate dataNascimento;

    private Double peso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    @JsonBackReference // <-- ADICIONE ESTA ANOTAÇÃO AQUI
    private Tutor tutor;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Consulta> consultas;
}