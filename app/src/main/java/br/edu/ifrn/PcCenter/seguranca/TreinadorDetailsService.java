package br.edu.ifrn.PcCenter.seguranca;

import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroTreinador;
import br.edu.ifrn.PcCenter.persistencia.repositorio.TreinadorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TreinadorDetailsService implements UserDetailsService {

    @Autowired
    private TreinadorRepo treinadorRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        CadastroTreinador treinador = treinadorRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Treinador não encontrado com e-mail: " + email));

        // Lógica para atribuir o papel
        List<GrantedAuthority> authorities;
        // Se o e-mail for de Ash, atribui ROLE_ADMIN
        if (treinador.getEmail().equals("ash.ketchum@example.com")) {
             authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
             // Caso contrário, atribui ROLE_USER (usuário comum)
             authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return new org.springframework.security.core.userdetails.User(
                treinador.getEmail(), 
                treinador.getSenha(), 
                authorities // Agora com a role definida
        );
    }
}