package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importar

@Entity
@Table(name = "veterinarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Veterinario {

    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "id")
    @JsonIgnoreProperties({"funcionarios", "veterinarios", "tutores"}) // Ignora perfis de User
    private User user;

    @Column(nullable = false, unique = true)
    private String crmv;

    @Column(nullable = false)
    private String especialidade;

    // CORRIGIDO: Adicionado @JsonIgnoreProperties para Consultas
    // Ignora a propriedade 'veterinario' DENTRO de CADA CONSULTA na lista 'consultas'.
    @OneToMany(mappedBy = "veterinario", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("veterinario") // <<< MUDANÇA AQUI
    private List<Consulta> consultas;

    // CORRIGIDO: Adicionado @JsonIgnoreProperties para DisponibilidadeVeterinario
    // Ignora a propriedade 'veterinario' DENTRO de CADA DISPONIBILIDADE na lista 'disponibilidades'.
    @OneToMany(mappedBy = "veterinario", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("veterinario") // <<< MUDANÇA AQUI
    private List<DisponibilidadeVeterinario> disponibilidades;
}