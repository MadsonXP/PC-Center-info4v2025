package br.edu.ifrn.PcCenter.web.controladores;

import br.edu.ifrn.PcCenter.persistencia.modelo.SolicitacaoTroca; 
import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroTreinador;
import br.edu.ifrn.PcCenter.persistencia.modelo.ListaInteresse;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/solicitacoes")
public class SolicitacaoTrocaControle {
    
    // 1. Adicionar o Logger para depuração
    private static final Logger logger = LoggerFactory.getLogger(SolicitacaoTrocaControle.class);

    @Autowired
    private SolicitacaoTrocaRepo solicitacaoTrocaRepo; // LINHA CORRIGIDA

    @Autowired
    private TreinadorRepo treinadorRepo; 
    
    @Autowired
    private PokemonRepo pokemonRepo;
    
    @Autowired
    private ListaInteresseRepo listaInteresseRepo;


    // Método auxiliar para buscar o Treinador logado
    private CadastroTreinador getTreinadorLogado() {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        return treinadorRepo.findByEmail(emailLogado)
            .orElseThrow(() -> new IllegalStateException("Erro: Treinador logado não encontrado no banco de dados."));
    }

    // MÉTODO LISTAR CORRIGIDO COM LOGS DE DEBUG
    @GetMapping
    public String listar(Model model) {
        CadastroTreinador treinadorLogado = getTreinadorLogado();
        Long treinadorId = treinadorLogado.getId();
        
        // LOG DE DEBUG PARA VERIFICAR O ID DO USUÁRIO LOGADO
        logger.info("Treinador Logado: {} (ID: {})", treinadorLogado.getNome(), treinadorId);
        
        // Estas consultas dependem da correção do Solicita\<\ctrl60>caoTrocaRepo com JOIN FETCH
        List<SolicitacaoTroca> enviadas = solicitacaoTrocaRepo.findByTreinadorSolicitanteId(treinadorId);
        List<SolicitacaoTroca> recebidas = solicitacaoTrocaRepo.findByTreinadorReceptorId(treinadorId);
        
        // LOG DE DEBUG PARA VERIFICAR OS RESULTADOS DA CONSULTA
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
        
        // Prepara os dados para o formulário
        model.addAttribute("proponente", proponente);
        model.addAttribute("meusPokemons", pokemonRepo.findByTreinadorId(proponente.getId()));
        
        // Adiciona as listas completas para os campos de seleção
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
    public String salvar(@Valid @ModelAttribute("solicitacao") SolicitacaoTroca solicitacao, BindingResult result) {
        CadastroTreinador proponente = getTreinadorLogado();
        
        solicitacao.setTreinadorSolicitante(proponente); 
        solicitacao.setDataSolicitacao(LocalDateTime.now()); 
        solicitacao.setStatusTroca(StatusTroca.PENDENTE); 

        // CRÍTICO: Re-injetar listas e objetos em caso de falha na validação
        if (result.hasErrors()) {
            solicitacao.setTreinadorSolicitante(proponente);
            
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