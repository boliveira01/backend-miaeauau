package com.miaueauau.clinica_veterinaria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "funcionarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    // Podemos adicionar informações sobre cargo, data de admissão, etc.
    private String cargo;

    // Informações de login (poderíamos criar uma entidade separada para segurança no futuro)
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    // Indica se o funcionário é um administrador (terá mais permissões)
    private boolean administrador;

    // --- NOVO: Relacionamento One-to-One com Veterinario ---
    @OneToOne(mappedBy = "funcionario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    // "mappedBy" indica o nome do campo na entidade Veterinario que é o dono do relacionamento.
    // cascade = CascadeType.ALL: Operações (salvar, atualizar, deletar) em Funcionario propagam para Veterinario associado.
    // orphanRemoval = true: Se um Veterinario for desassociado deste Funcionario, ele é deletado do banco.
    // FetchType.LAZY: O objeto Veterinario só será carregado quando for acessado.
    private Veterinario veterinario;

    // Podemos adicionar mais campos relevantes para o funcionário no futuro
}