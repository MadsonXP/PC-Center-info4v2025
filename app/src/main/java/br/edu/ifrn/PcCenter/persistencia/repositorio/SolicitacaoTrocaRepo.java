package br.edu.ifrn.PcCenter.persistencia.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ifrn.PcCenter.persistencia.modelo.SolicitacaoTroca;

public interface SolicitacaoTrocaRepo extends JpaRepository<SolicitacaoTroca, Long> {
}