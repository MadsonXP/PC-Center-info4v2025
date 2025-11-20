package br.edu.ifrn.PcCenter.persistencia.repositorio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroTreinador;

public interface TreinadorRepo extends JpaRepository<CadastroTreinador, Long> {

    // Método padrão: Usado para busca exata (Login e Cadastro)
    Optional<CadastroTreinador> findByEmail(String email);
    // Remover qualquer outro método findByEmail...
}