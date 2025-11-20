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
        // 1. Busca o Treinador pelo e-mail
        CadastroTreinador treinador = treinadorRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Treinador não encontrado com e-mail: " + email));

        // 2. Converte o objeto Treinador para o formato UserDetails
        // O construtor do Spring Security User espera: username (email), password (senha), e authorities (lista vazia)
        return new org.springframework.security.core.userdetails.User(
                treinador.getEmail(), 
                treinador.getSenha(), // Pega a senha CODIFICADA do objeto
                emptyList() 
        );
    }
}