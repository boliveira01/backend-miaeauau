package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
@Table(name = "procedimentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Procedimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "O nome do procedimento é obrigatório")
    @Size(max = 255, message = "O nome do procedimento não pode ter mais de 255 caracteres")
    private String nome;

    @Size(max = 1000, message = "A descrição do procedimento não pode ter mais de 1000 caracteres")
    private String descricao;

    @NotNull(message = "O preço do procedimento é obrigatório")
    @Positive(message = "O preço do procedimento deve ser um valor positivo")
    private Double preco;

    // CORRIGIDO: Adicionado @JsonIgnoreProperties para Consultas
    // Ignora a propriedade 'procedimentos' DENTRO de CADA CONSULTA da lista 'consultas'.
    @ManyToMany(mappedBy = "procedimentos", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("procedimentos") // <<-- ADICIONE ESTA ANOTAÇÃO AQUI
    private List<Consulta> consultas;
}