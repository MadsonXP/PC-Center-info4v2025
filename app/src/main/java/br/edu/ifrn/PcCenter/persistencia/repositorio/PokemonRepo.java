package br.edu.ifrn.PcCenter.persistencia.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroPokemon;

public interface PokemonRepo extends JpaRepository<CadastroPokemon, Long> {
}