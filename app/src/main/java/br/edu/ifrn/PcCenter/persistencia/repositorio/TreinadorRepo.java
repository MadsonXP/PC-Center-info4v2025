package br.edu.ifrn.PcCenter.persistencia.repositorio;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroTreinador;

public interface TreinadorRepo extends JpaRepository<CadastroTreinador, Long> {

    // Assinatura de método para encontrar o Treinador por e-mail
    Optional<CadastroTreinador> findByEmail(String email);
}