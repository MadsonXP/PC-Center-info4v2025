package br.edu.ifrn.PcCenter.config;

import br.edu.ifrn.PcCenter.persistencia.repositorio.TreinadorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class SecurityAdvice {

    @Autowired
    private TreinadorRepo treinadorRepo;

    // Este método é executado antes de renderizar qualquer view
    @ModelAttribute("nomeUsuario")
    public String nomeUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Verifica se há um usuário autenticado (e não é o usuário anônimo)
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String email = auth.getName();
            
            // Busca o nome completo do Treinador no banco usando o e-mail
            return treinadorRepo.findByEmail(email)
                    .map(treinador -> treinador.getNome())
                    .orElse(email); // Se não encontrar o nome, retorna o email
        }
        return null; 
    }
}