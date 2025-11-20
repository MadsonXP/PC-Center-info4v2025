package br.edu.ifrn.PcCenter.persistencia.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ifrn.PcCenter.persistencia.modelo.SolicitacaoTroca;

public interface SolicitacaoTrocaRepo extends JpaRepository<SolicitacaoTroca, Long> {
    
    // CORREÇÃO: Método para buscar solicitações enviadas pelo Treinador (o Controller precisa deste)
    List<SolicitacaoTroca> findByTreinadorSolicitanteId(Long treinadorId);

    // Método para buscar solicitações recebidas pelo Treinador (o Controller precisa deste)
    List<SolicitacaoTroca> findByTreinadorReceptorId(Long treinadorId);
}