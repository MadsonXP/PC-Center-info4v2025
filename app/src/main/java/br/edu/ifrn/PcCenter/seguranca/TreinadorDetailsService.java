package br.edu.ifrn.PcCenter.seguranca;

import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroTreinador;
import br.edu.ifrn.PcCenter.persistencia.repositorio.TreinadorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

@Service
public class TreinadorDetailsService implements UserDetailsService {

    @Autowired
    private TreinadorRepo treinadorRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca o Treinador pelo e-mail
        CadastroTreinador treinador = treinadorRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Treinador não encontrado com e-mail: " + email));

        // Converte o objeto Treinador para o formato UserDetails que o Spring entende
        return new org.springframework.security.core.userdetails.User(
                treinador.getEmail(), 
                treinador.getSenha(), // A senha já está codificada no banco
                emptyList() 
        );
    }
}