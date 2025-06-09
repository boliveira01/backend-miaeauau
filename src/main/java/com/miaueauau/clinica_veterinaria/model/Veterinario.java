package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference; // <-- NOVO IMPORT

import java.util.List;

@Entity
@Table(name = "veterinarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Veterinario {

    @Id
    @Column(name = "funcionario_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "funcionario_id")
    private Funcionario funcionario;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String crmv;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private String especialidade;

    @Column(nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "veterinario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference // <-- ADICIONE ESTA ANOTAÇÃO AQUI
    private List<DisponibilidadeVeterinario> disponibilidades;

    @OneToMany(mappedBy = "veterinario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference // <-- ADICIONE ESTA ANOTAÇÃO AQUI
    private List<Consulta> consultas;
}