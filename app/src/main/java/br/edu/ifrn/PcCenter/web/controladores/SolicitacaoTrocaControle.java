package br.edu.ifrn.PcCenter.web.controladores;

import br.edu.ifrn.PcCenter.persistencia.modelo.SolicitacaoTroca;
import br.edu.ifrn.PcCenter.persistencia.repositorio.SolicitacaoTrocaRepo;
import br.edu.ifrn.PcCenter.persistencia.repositorio.TreinadorRepo;
import br.edu.ifrn.PcCenter.persistencia.repositorio.PokemonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/solicitacoes")
public class SolicitacaoTrocaControle {

    @Autowired
    private SolicitacaoTrocaRepo solicitacaoTrocaRepo;

    @Autowired
    private TreinadorRepo treinadorRepo;

    @Autowired
    private PokemonRepo pokemonRepo;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("solicitacoes", solicitacaoTrocaRepo.findAll());
        return "Solicitacao/lista-solicitacao";
    }

    @GetMapping("/novo")
    public String formulario(Model model) {
        SolicitacaoTroca novaSolicitacao = new SolicitacaoTroca();
        
        novaSolicitacao.prePersist(); 
        
        model.addAttribute("solicitacao", novaSolicitacao);
        
        model.addAttribute("treinadores", treinadorRepo.findAll());
        model.addAttribute("pokemons", pokemonRepo.findAll());
        
        return "Solicitacao/formulario-solicitacao";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("solicitacao") SolicitacaoTroca solicitacao, BindingResult result) {
        if (result.hasErrors()) {
            // Recarrega as listas em caso de erro de validação
            result.getModel().put("treinadores", treinadorRepo.findAll());
            result.getModel().put("pokemons", pokemonRepo.findAll());
            return "Solicitacao/formulario-solicitacao";
        }

        solicitacaoTrocaRepo.save(solicitacao);
        return "redirect:/solicitacoes";
    }
}