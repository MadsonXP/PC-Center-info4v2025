package br.edu.ifrn.PcCenter.persistencia.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import br.edu.ifrn.PcCenter.persistencia.modelo.ListaInteresse;

public interface ListaInteresseRepo extends JpaRepository<ListaInteresse, Long> {
}