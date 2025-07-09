package br.edu.ifrn.PcCenter.persistencia.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifrn.PcCenter.persistencia.modelo.Ala;

public interface TreinadorRepo extends JpaRepository<CadastroTreinador, Long> {

    Optional<Ala> findByDescricao(String descricao);

}