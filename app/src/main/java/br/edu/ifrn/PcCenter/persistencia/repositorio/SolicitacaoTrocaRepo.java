package br.edu.ifrn.PcCenter.persistencia.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import br.edu.ifrn.PcCenter.persistencia.modelo.SolicitacaoTroca;

public interface SolicitacaoTrocaRepo extends JpaRepository<SolicitacaoTroca, Long> {
    
    // Correção: Usar JOIN FETCH para carregar todos os relacionamentos
    @Query("SELECT s FROM SolicitacaoTroca s " +
           "LEFT JOIN FETCH s.treinadorSolicitante ts " +
           "LEFT JOIN FETCH s.pokemonOferecido po " +
           "LEFT JOIN FETCH s.treinadorReceptor tr " +
           "LEFT JOIN FETCH s.pokemonSolicitado ps " +
           "WHERE s.treinadorSolicitante.id = :treinadorId")
    List<SolicitacaoTroca> findByTreinadorSolicitanteId(@Param("treinadorId") Long treinadorId);

    // Correção: Usar JOIN FETCH para carregar todos os relacionamentos
    @Query("SELECT s FROM SolicitacaoTroca s " +
           "LEFT JOIN FETCH s.treinadorSolicitante ts " +
           "LEFT JOIN FETCH s.pokemonOferecido po " +
           "LEFT JOIN FETCH s.treinadorReceptor tr " +
           "LEFT JOIN FETCH s.pokemonSolicitado ps " +
           "WHERE s.treinadorReceptor.id = :treinadorId")
    List<SolicitacaoTroca> findByTreinadorReceptorId(@Param("treinadorId") Long treinadorId);
}