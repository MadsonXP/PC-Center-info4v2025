package br.edu.ifrn.PcCenter.persistencia.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ifrn.PcCenter.persistencia.modelo.ListaInteresse;

public interface ListaInteresseRepo extends JpaRepository<ListaInteresse, Long> {
    
    // Método para buscar interesses que pertencem a um Treinador (Meus Interesses)
    List<ListaInteresse> findByTreinadorId(Long treinadorId);

    // NOVO: Método para buscar interesses que NÃO pertencem a um Treinador (Outros Treinadores)
    List<ListaInteresse> findByTreinadorIdNot(Long treinadorId);
}