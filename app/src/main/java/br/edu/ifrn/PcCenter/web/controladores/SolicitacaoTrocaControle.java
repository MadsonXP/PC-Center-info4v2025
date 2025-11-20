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

// IMPORTES PARA LOGGING
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// IMPORTES PARA BINDING
import org.springframework.web.bind.WebDataBinder;
import java.beans.PropertyEditorSupport;

// IMPORTANTE: Adicionar @Transactional para garantir o commit
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


    // MÉTODO CRÍTICO: Configura o binder para converter IDs em Objetos (Treinador e Pokémon)
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // 1. Binder para CadastroTreinador (para treinadorReceptor)
        binder.registerCustomEditor(CadastroTreinador.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text != null && !text.isEmpty()) {
                    try {
                        Long id = Long.valueOf(text);
                        // Usa o findById do seu TreinadorRepo
                        CadastroTreinador treinador = treinadorRepo.findById(id).orElse(null);
                        setValue(treinador);
                    } catch (NumberFormatException e) {
                        logger.error("Erro de formato ao converter ID de Treinador: {}", text);
                        setValue(null);
                    }
                } else {
                    setValue(null);
                }
            }
        });

        // 2. Binder para CadastroPokemon (para pokemonOferecido e pokemonSolicitado)
        binder.registerCustomEditor(CadastroPokemon.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text != null && !text.isEmpty()) {
                    try {
                        Long id = Long.valueOf(text);
                        // Usa o findById do seu PokemonRepo
                        CadastroPokemon pokemon = pokemonRepo.findById(id).orElse(null);
                        setValue(pokemon);
                    } catch (NumberFormatException e) {
                        logger.error("Erro de formato ao converter ID de Pokémon: {}", text);
                        setValue(null); 
                    }
                } else {
                    setValue(null);
                }
            }
        });
    }
    
    
    private CadastroTreinador getTreinadorLogado() {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        return treinadorRepo.findByEmail(emailLogado)
            .orElseThrow(() -> new IllegalStateException("Erro: Treinador logado não encontrado no banco de dados."));
    }

    @GetMapping
    public String listar(Model model) {
        CadastroTreinador treinadorLogado = getTreinadorLogado();
        Long treinadorId = treinadorLogado.getId();
        
        logger.info("Treinador Logado: {} (ID: {})", treinadorLogado.getNome(), treinadorId);
        
        List<SolicitacaoTroca> enviadas = solicitacaoTrocaRepo.findByTreinadorSolicitanteId(treinadorId);
        List<SolicitacaoTroca> recebidas = solicitacaoTrocaRepo.findByTreinadorReceptorId(treinadorId);
        
        logger.info("Solicitações Enviadas encontradas: {}", enviadas.size());
        logger.info("Solicitações Recebidas encontradas: {}", recebidas.size());
        
        model.addAttribute("nomeUsuario", treinadorLogado.getNome());
        model.addAttribute("enviadas", enviadas); 
        model.addAttribute("recebidas", recebidas); 
        
        return "Solicitacao/lista-solicitacao"; 
    }
    
    @GetMapping("/novo")
    public String formulario(@RequestParam(required = false) Long interesseId, Model model) {
        CadastroTreinador proponente = getTreinadorLogado();
        SolicitacaoTroca solicitacao = new SolicitacaoTroca();
        
        model.addAttribute("nomeUsuario", proponente.getNome());
        model.addAttribute("solicitacao", solicitacao);
        
        model.addAttribute("proponente", proponente);
        model.addAttribute("meusPokemons", pokemonRepo.findByTreinadorId(proponente.getId()));
        
        model.addAttribute("todosTreinadores", treinadorRepo.findAll()); 
        model.addAttribute("todosPokemons", pokemonRepo.findAll());
        
        if (interesseId != null) {
            Optional<ListaInteresse> interesseOpt = listaInteresseRepo.findById(interesseId);
            if (interesseOpt.isPresent()) {
                ListaInteresse interesse = interesseOpt.get();
                
                solicitacao.setTreinadorReceptor(interesse.getTreinador());
                
                model.addAttribute("interesseSelecionado", interesse);
                model.addAttribute("receptorSelecionado", interesse.getTreinador());
            }
        } else {
             model.addAttribute("todosInteresses", listaInteresseRepo.findAll());
        }
        
        return "Solicitacao/formulario-solicitacao";
    }

    @PostMapping
    @Transactional 
    public String salvar(@Valid @ModelAttribute("solicitacao") SolicitacaoTroca solicitacao, BindingResult result) {
        CadastroTreinador proponente = getTreinadorLogado();
        
        // Asseguramos que o treinador solicitante é injetado antes da validação da JPA
        solicitacao.setTreinadorSolicitante(proponente); 
        solicitacao.setDataSolicitacao(LocalDateTime.now()); 
        solicitacao.setStatusTroca(StatusTroca.PENDENTE); 

        // CRÍTICO: Se a validação falhar, o Proponente e as listas são perdidos, precisamos re-injetá-los
        if (result.hasErrors()) {
            logger.error("Erro de validação ao salvar solicitação: {}", result.getAllErrors()); // Loga os erros
            
            // Re-injeta todas as listas que foram adicionadas no método GET
            result.getModel().put("nomeUsuario", proponente.getNome());
            result.getModel().put("proponente", proponente);
            result.getModel().put("meusPokemons", pokemonRepo.findByTreinadorId(proponente.getId()));
            result.getModel().put("todosTreinadores", treinadorRepo.findAll());
            result.getModel().put("todosPokemons", pokemonRepo.findAll());
            
            // Recarrega as listas de apoio para evitar falhas no HTML
            if (solicitacao.getPokemonSolicitado() != null) {
                 result.getModel().put("interesseSelecionado", solicitacao.getPokemonSolicitado());
                 result.getModel().put("receptorSelecionado", solicitacao.getTreinadorReceptor());
            } else {
                 result.getModel().put("todosInteresses", listaInteresseRepo.findAll());
            }
            
            return "Solicitacao/formulario-solicitacao";
        }
        
        solicitacaoTrocaRepo.save(solicitacao);
        logger.info("Solicitação de Troca salva com sucesso (ID: {})", solicitacao.getId());
        return "redirect:/solicitacoes";
    }
}