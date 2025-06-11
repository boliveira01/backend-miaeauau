package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIdentityInfo; // NOVO
import com.fasterxml.jackson.annotation.ObjectIdGenerators; // NOVO
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importar

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
@Table(name = "tutores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tutor {

    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "id")
    @JsonIgnoreProperties({"funcionarios", "veterinarios", "tutores"}) // Ignora outros perfis dentro do User
    private User user;

    private LocalDate dataNascimento;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false)
    private String telefone;

    // Manter LAZY, e JsonIgnoreProperties para Pacientes
    @OneToMany(mappedBy = "tutor", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("tutor") // Ignora o Tutor dentro de cada Paciente na lista (lado inverso)
    private List<Paciente> pacientes = new ArrayList<>();
}