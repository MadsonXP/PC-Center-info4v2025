package br.edu.ifrn.PcCenter.persistencia.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat; // <<--- ESTE IMPORT É CRÍTICO

@Entity
@Table(name = "lista_interesses")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListaInteresse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A validação @NotNull aqui exige que o campo seja preenchido no Controller
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "treinador_id", nullable = false)
    @NotNull(message = "O treinador é obrigatório.")
    private CadastroTreinador treinador;

    @Column(name = "pokemon_desejado_nome", nullable = false, length = 100)
    @NotBlank(message = "O nome do Pokémon desejado é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    private String pokemonDesejadoNome;

    @Column(name = "pokemon_desejado_tipo", length = 50)
    @Size(max = 50, message = "O tipo deve ter no máximo 50 caracteres.")
    private String pokemonDesejadoTipo;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "data_interesse", nullable = false)
    @NotNull(message = "A data de interesse é obrigatória.")
    @DateTimeFormat(pattern = "yyyy-MM-dd") // <<--- CORREÇÃO DE FORMATAÇÃO
    private LocalDate dataInteresse; 
}