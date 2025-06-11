package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// REMOVER: import com.fasterxml.jackson.annotation.JsonBackReference;
// REMOVER: import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "funcionarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario {

    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @MapsId
    @JoinColumn(name = "id")
    // REMOVER: @JsonManagedReference("user-funcionario")
    private User user;

    @Column(nullable = false)
    private String cargo;

    private boolean administrador;
}