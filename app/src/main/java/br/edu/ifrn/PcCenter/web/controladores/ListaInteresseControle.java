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
import java.util.Collections;
import java.util.List; 

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

    // MÉTODO CORRIGIDO: Separa Interesses em duas listas
    @GetMapping
    public String listar(Model model) {
        CadastroTreinador treinadorLogado = getTreinadorLogado();
        Long treinadorId = treinadorLogado.getId();
        
        // 1. Busca os Interesses do próprio Treinador
        List<ListaInteresse> meusInteresses = listaInteresseRepo.findByTreinadorId(treinadorId);
        // 2. Busca os Interesses de outros Treinadores
        List<ListaInteresse> outrosInteresses = listaInteresseRepo.findByTreinadorIdNot(treinadorId);
        
        model.addAttribute("nomeUsuario", treinadorLogado.getNome());
        model.addAttribute("meusInteresses", meusInteresses); // Lista para a primeira tabela
        model.addAttribute("outrosInteresses", outrosInteresses); // Lista para a segunda tabela
        
        return "Interesse/lista-interesse"; 
    }

    @GetMapping("/novo")
    public String formulario(Model model) {
        CadastroTreinador treinadorLogado = getTreinadorLogado();
        
        model.addAttribute("nomeUsuario", treinadorLogado.getNome());
        model.addAttribute("interesse", new ListaInteresse());
        
        // CORREÇÃO CRÍTICA: Envia apenas o treinador logado para o binding (campo oculto)
        model.addAttribute("treinadores", Collections.singletonList(treinadorLogado)); 
        
        return "Interesse/formulario-interesse"; 
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("interesse") ListaInteresse interesse, BindingResult result) {
        
        // CORREÇÃO DE PERSISTÊNCIA: Em caso de erro de validação, reinjeta o Treinador
        if (result.hasErrors()) {
            CadastroTreinador treinadorLogado = getTreinadorLogado();
            result.getModel().put("nomeUsuario", treinadorLogado.getNome());
            result.getModel().put("treinadores", Collections.singletonList(treinadorLogado));
            return "Interesse/formulario-interesse";
        }
        
        listaInteresseRepo.save(interesse);
        return "redirect:/interesses";
    }
    
    // NOTA: Adicione os métodos editar e excluir aqui se eles existirem ou forem necessários.
    // Exemplo de Excluir:
    /*
    @GetMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        listaInteresseRepo.deleteById(id);
        return "redirect:/interesses";
    }
    */
}