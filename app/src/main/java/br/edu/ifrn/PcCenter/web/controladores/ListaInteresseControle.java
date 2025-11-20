package br.edu.ifrn.PcCenter.web.controladores;

import br.edu.ifrn.PcCenter.persistencia.modelo.ListaInteresse;
import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroTreinador;
import br.edu.ifrn.PcCenter.persistencia.repositorio.ListaInteresseRepo;
import br.edu.ifrn.PcCenter.persistencia.repositorio.TreinadorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.Collections; // Import necessário

@Controller
@RequestMapping("/interesses")
public class ListaInteresseControle {

    @Autowired
    private ListaInteresseRepo listaInteresseRepo;

    @Autowired
    private TreinadorRepo treinadorRepo; 

    // Método auxiliar para buscar o Treinador logado
    private CadastroTreinador getTreinadorLogado() {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        return treinadorRepo.findByEmail(emailLogado)
            .orElseThrow(() -> new IllegalStateException("Erro: Treinador logado não encontrado no banco de dados."));
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("nomeUsuario", getTreinadorLogado().getNome());
        model.addAttribute("interesses", listaInteresseRepo.findAll());
        return "Interesse/lista-interesse"; 
    }

    @GetMapping("/novo")
    public String formulario(Model model) {
        CadastroTreinador treinadorLogado = getTreinadorLogado();
        
        model.addAttribute("nomeUsuario", treinadorLogado.getNome());
        model.addAttribute("interesse", new ListaInteresse());
        
        // CORREÇÃO CRÍTICA: Envia APENAS o treinador logado em uma lista para o binding no HTML
        model.addAttribute("treinadores", Collections.singletonList(treinadorLogado)); 
        
        return "Interesse/formulario-interesse"; 
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("interesse") ListaInteresse interesse, BindingResult result) {
        
        // Se a validação falhar, o Treinador ID é perdido. Precisamos re-anexá-lo ao modelo.
        if (result.hasErrors()) {
            CadastroTreinador treinadorLogado = getTreinadorLogado();
            result.getModel().put("nomeUsuario", treinadorLogado.getNome());
            // Reenvia a lista de treinadores (com um único item)
            result.getModel().put("treinadores", Collections.singletonList(treinadorLogado));
            return "Interesse/formulario-interesse";
        }
        
        // A associação é feita automaticamente pelo Thymeleaf no campo oculto do HTML
        listaInteresseRepo.save(interesse);
        return "redirect:/interesses";
    }
}