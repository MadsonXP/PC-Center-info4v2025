package br.edu.ifrn.PcCenter.persistencia.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroPokemon;

public interface PokemonRepo extends JpaRepository<CadastroPokemon, Long> {
    // Método para buscar Pokémons que pertencem a um Treinador
    List<CadastroPokemon> findByTreinadorId(Long treinadorId);

    // NOVO: Método para buscar Pokémons que NÃO pertencem a um Treinador
    List<CadastroPokemon> findByTreinadorIdNot(Long treinadorId);
}