package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importar

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

    // FetchType.EAGER para carregar o tutor imediatamente
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tutor_id", nullable = false)
    @JsonIgnoreProperties("pacientes") // Ignora a lista de pacientes dentro do Tutor (lado inverso)
    private Tutor tutor;

    @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("paciente") // Ignora o Paciente dentro da Consulta (lado inverso)
    private List<Consulta> consultas;
}