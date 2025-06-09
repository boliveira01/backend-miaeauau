package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;  // Importe para campos de texto não vazios
import jakarta.validation.constraints.NotNull;   // Importe para campos não nulos
import jakarta.validation.constraints.Positive;  // Importe para números positivos
import jakarta.validation.constraints.Size;    // Importe para controle de tamanho de strings
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "procedimentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Procedimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // NOVO: Nome do procedimento geralmente é único
    @NotBlank(message = "O nome do procedimento é obrigatório") // NOVO: Nome não pode ser vazio ou nulo
    @Size(max = 255, message = "O nome do procedimento não pode ter mais de 255 caracteres") // NOVO: Limita o tamanho do nome
    private String nome;

    @Size(max = 1000, message = "A descrição do procedimento não pode ter mais de 1000 caracteres") // NOVO: Limita o tamanho da descrição
    private String descricao;

    @NotNull(message = "O preço do procedimento é obrigatório") // NOVO: Preço não pode ser nulo
    @Positive(message = "O preço do procedimento deve ser um valor positivo") // NOVO: Preço deve ser positivo
    private Double preco;

    @ManyToMany(mappedBy = "procedimentos", fetch = FetchType.LAZY) // NOVO: FetchType.LAZY para evitar carregamento ansioso desnecessário
    private List<Consulta> consultas;

    // Outros campos relevantes para Procedimento
}