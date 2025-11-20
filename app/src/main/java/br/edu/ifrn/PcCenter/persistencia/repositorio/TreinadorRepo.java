package br.edu.ifrn.PcCenter.persistencia.repositorio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroTreinador;

public interface TreinadorRepo extends JpaRepository<CadastroTreinador, Long> {

    // MÉTODO CRÍTICO: Usado para buscar o Treinador pelo e-mail (Username)
    Optional<CadastroTreinador> findByEmail(String email);
}