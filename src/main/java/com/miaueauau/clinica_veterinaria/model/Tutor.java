package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference; // <-- NOVO IMPORT

@Entity
@Table(name = "tutores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDate dataNascimento;

    @OneToMany(mappedBy = "tutor",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY) // <-- Adicionei FetchType.LAZY para melhor performance, se não tiver
    @JsonManagedReference // <-- ADICIONE ESTA ANOTAÇÃO AQUI
    private List<Paciente> pacientes = new ArrayList<>();
}