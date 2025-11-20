package br.edu.ifrn.PcCenter.persistencia.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

@Entity
@Table(name = "pokemons")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CadastroPokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CORREÇÃO: Usando FetchType.EAGER para evitar LazyInitializationException no Thymeleaf
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "treinador_id", nullable = false)
    @NotNull(message = "O treinador é obrigatório.")
    private CadastroTreinador treinador;

    @Column(name = "nome", nullable = false, length = 100)
    @NotBlank(message = "O nome do Pokémon é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String nome;

    @Column(name = "tipo", nullable = false, length = 50)
    @NotBlank(message = "O tipo é obrigatório.")
    @Size(max = 50, message = "O tipo deve ter no máximo 50 caracteres.")
    private String tipo;

    @Column(name = "habilidade", length = 100)
    @Size(max = 100, message = "A habilidade deve ter no máximo 100 caracteres.")
    private String habilidade;

    @Column(name = "nivel")
    @NotNull(message = "O nível é obrigatório.")
    @Min(value = 1, message = "O nível deve ser no mínimo 1.")
    private Integer nivel;
}