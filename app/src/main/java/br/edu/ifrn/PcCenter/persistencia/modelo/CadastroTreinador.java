package br.edu.ifrn.PcCenter.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate; // Importe para usar LocalDate para a data de cadastro

@Entity
@Table(name = "treinadores") // Nome da tabela no banco de dados
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CadastroTreinador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "senha", nullable = false, length = 255) // Para armazenar o hash da senha
    private String senha;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDate dataCadastro; // Usando LocalDate para a data

}