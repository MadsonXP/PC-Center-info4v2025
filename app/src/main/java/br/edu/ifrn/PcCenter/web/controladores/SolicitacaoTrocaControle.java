package br.edu.ifrn.PcCenter.web.controladores;

import br.edu.ifrn.PcCenter.persistencia.modelo.SolicitacaoTroca; 
import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroTreinador;
import br.edu.ifrn.PcCenter.persistencia.modelo.ListaInteresse;
import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroPokemon;
import br.edu.ifrn.PcCenter.persistencia.modelo.StatusTroca; 

import br.edu.ifrn.PcCenter.persistencia.repositorio.SolicitacaoTrocaRepo;
import br.edu.ifrn.PcCenter.persistencia.repositorio.TreinadorRepo;
import br.edu.ifrn.PcCenter.persistencia.repositorio.PokemonRepo;
import br.edu.ifrn.PcCenter.persistencia.repositorio.ListaInteresseRepo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import java.beans.PropertyEditorSupport;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/solicitacoes")
public class SolicitacaoTrocaControle {
    
    private static final Logger logger = LoggerFactory.getLogger(SolicitacaoTrocaControle.class);

    @Autowired
    private SolicitacaoTrocaRepo solicitacaoTrocaRepo; 

    @Autowired
    private TreinadorRepo treinadorRepo; 
    
    @Autowired
    private PokemonRepo pokemonRepo;
    
    @Autowired
    private ListaInteresseRepo listaInteresseRepo;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(CadastroTreinador.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                try { setValue(treinadorRepo.findById(Long.parseLong(text)).orElse(null)); }
                catch (Exception e) { setValue(null); }
            }
        });
        binder.registerCustomEditor(CadastroPokemon.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                try { setValue(pokemonRepo.findById(Long.parseLong(text)).orElse(null)); }
                catch (Exception e) { setValue(null); }
            }
        });
    }
    
    private CadastroTreinador getTreinadorLogado() {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        return treinadorRepo.findByEmail(emailLogado)
            .orElseThrow(() -> new IllegalStateException("Erro: Treinador logado não encontrado."));
    }

    @GetMapping
    public String listar(Model model) {
        CadastroTreinador treinadorLogado = getTreinadorLogado();
        Long treinadorId = treinadorLogado.getId();
        
        List<SolicitacaoTroca> enviadas = solicitacaoTrocaRepo.findByTreinadorSolicitanteId(treinadorId);
        List<SolicitacaoTroca> recebidas = solicitacaoTrocaRepo.findByTreinadorReceptorId(treinadorId);
        
        model.addAttribute("nomeUsuario", treinadorLogado.getNome());
        model.addAttribute("enviadas", enviadas); 
        model.addAttribute("recebidas", recebidas); 
        
        return "Solicitacao/lista-solicitacao"; 
    }
    
    @GetMapping("/novo")
    public String formulario(@RequestParam(required = false) Long interesseId, Model model) {
        CadastroTreinador proponente = getTreinadorLogado();
        SolicitacaoTroca solicitacao = new SolicitacaoTroca();
        
        prepararModelParaFormulario(model, proponente);
        
        if (interesseId != null) {
            Optional<ListaInteresse> interesseOpt = listaInteresseRepo.findById(interesseId);
            if (interesseOpt.isPresent()) {
                ListaInteresse interesse = interesseOpt.get();
                solicitacao.setTreinadorReceptor(interesse.getTreinador());
                model.addAttribute("interesseSelecionado", interesse);
                model.addAttribute("receptorSelecionado", interesse.getTreinador());
            }
        }
        model.addAttribute("solicitacao", solicitacao);
        return "Solicitacao/formulario-solicitacao";
    }

    @PostMapping
    @Transactional 
    public String salvar(@Valid @ModelAttribute("solicitacao") SolicitacaoTroca solicitacao, BindingResult result, Model model) {
        CadastroTreinador proponente = getTreinadorLogado();
        
        solicitacao.setTreinadorSolicitante(proponente); 
        if (solicitacao.getDataSolicitacao() == null) solicitacao.setDataSolicitacao(LocalDateTime.now()); 
        solicitacao.setStatusTroca(StatusTroca.PENDENTE); 

        if (result.hasErrors()) {
            prepararModelParaFormulario(model, proponente);
            return "Solicitacao/formulario-solicitacao";
        }
        
        solicitacaoTrocaRepo.save(solicitacao);
        return "redirect:/solicitacoes";
    }

    // --- NOVOS MÉTODOS PARA ACEITAR/RECUSAR ---

    @GetMapping("/{id}/aceitar")
    @Transactional // Garante que todas as operações no banco ocorram juntas ou nenhuma ocorra
    public String aceitarTroca(@PathVariable Long id) {
        SolicitacaoTroca solicitacao = solicitacaoTrocaRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Solicitação inválida"));
            
        // 1. Atualiza o Status
        solicitacao.setStatusTroca(StatusTroca.ACEITA);
        
        // 2. Lógica de Troca (Swap) dos Pokémons
        CadastroPokemon pokemonOferecido = solicitacao.getPokemonOferecido();
        CadastroPokemon pokemonSolicitado = solicitacao.getPokemonSolicitado();
        
        CadastroTreinador solicitante = solicitacao.getTreinadorSolicitante();
        CadastroTreinador receptor = solicitacao.getTreinadorReceptor(); // Quem está aceitando
        
        // A: O Receptor (quem aceitou) ganha o Pokémon Oferecido
        pokemonOferecido.setTreinador(receptor);
        pokemonRepo.save(pokemonOferecido);
        
        // B: O Solicitante (quem pediu) ganha o Pokémon Solicitado (se houver)
        if (pokemonSolicitado != null) {
            pokemonSolicitado.setTreinador(solicitante);
            pokemonRepo.save(pokemonSolicitado);
        }
        
        solicitacaoTrocaRepo.save(solicitacao);
        return "redirect:/solicitacoes";
    }

    @GetMapping("/{id}/recusar")
    @Transactional
    public String recusarTroca(@PathVariable Long id) {
        SolicitacaoTroca solicitacao = solicitacaoTrocaRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Solicitação inválida"));
            
        solicitacao.setStatusTroca(StatusTroca.RECUSADA);
        solicitacaoTrocaRepo.save(solicitacao);
        
        return "redirect:/solicitacoes";
    }

    private void prepararModelParaFormulario(Model model, CadastroTreinador proponente) {
        model.addAttribute("nomeUsuario", proponente.getNome());
        model.addAttribute("proponente", proponente);
        model.addAttribute("meusPokemons", pokemonRepo.findByTreinadorId(proponente.getId()));
        model.addAttribute("todosTreinadores", treinadorRepo.findAll()); 
        model.addAttribute("todosPokemons", pokemonRepo.findAll());
        model.addAttribute("todosInteresses", listaInteresseRepo.findAll());
    }
}