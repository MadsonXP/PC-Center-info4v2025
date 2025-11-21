package br.edu.ifrn.PcCenter.persistencia.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacoes_troca")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SolicitacaoTroca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Treinador Solicitante
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "treinador_solicitante_id", nullable = false)
    private CadastroTreinador treinadorSolicitante;

    // Pokémon Oferecido
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pokemon_oferecido_id", nullable = false)
    @NotNull(message = "O Pokémon oferecido é obrigatório.")
    private CadastroPokemon pokemonOferecido;

    // Treinador Receptor
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "treinador_receptor_id", nullable = false)
    @NotNull(message = "O treinador receptor é obrigatório.")
    private CadastroTreinador treinadorReceptor;

    // Pokémon Solicitado (Opcional)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pokemon_solicitado_id") 
    private CadastroPokemon pokemonSolicitado;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_troca", nullable = false)
    // REMOVIDO @NotNull daqui para evitar erro de validação no formulário
    private StatusTroca statusTroca;

    @Column(name = "data_solicitacao", nullable = false)
    // REMOVIDO @NotNull daqui para evitar erro de validação no formulário
    private LocalDateTime dataSolicitacao;

    @PrePersist
    public void prePersist() {
        if (statusTroca == null) {
            statusTroca = StatusTroca.PENDENTE;
        }
        if (dataSolicitacao == null) {
            dataSolicitacao = LocalDateTime.now();
        }
    }
}