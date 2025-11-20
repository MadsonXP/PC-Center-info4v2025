package br.edu.ifrn.PcCenter.web.controladores;

import br.edu.ifrn.PcCenter.persistencia.modelo.SolicitacaoTroca;
import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroTreinador; // Import necessário
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
        
        // MANTIDO: Listas o receptor e os Pokémons
        model.addAttribute("treinadores", treinadorRepo.findAll());
        model.addAttribute("pokemons", pokemonRepo.findAll());
        
        return "Solicitacao/formulario-solicitacao";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("solicitacao") SolicitacaoTroca solicitacao, BindingResult result) {
        
        // CORREÇÃO LOGÍSTICA/VALIDAÇÃO: Associa o Treinador ID 1 ao Solicitante antes de checar erros
        Long treinadorLogadoId = 1L; 
        CadastroTreinador treinadorLogado = treinadorRepo.findById(treinadorLogadoId)
            .orElseThrow(() -> new IllegalStateException("Erro: Treinador logado não encontrado!"));

        // Define o Treinador Solicitante automaticamente
        solicitacao.setTreinadorSolicitante(treinadorLogado); 
        
        if (result.hasErrors()) {
            // Recarrega as listas de apoio em caso de erro de validação
            result.getModel().put("treinadores", treinadorRepo.findAll());
            result.getModel().put("pokemons", pokemonRepo.findAll()); 
            return "Solicitacao/formulario-solicitacao";
        }

        solicitacaoTrocaRepo.save(solicitacao);
        return "redirect:/solicitacoes";
    }
}