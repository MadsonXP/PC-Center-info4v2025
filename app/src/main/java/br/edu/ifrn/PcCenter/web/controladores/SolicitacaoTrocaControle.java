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
import org.springframework.web.servlet.mvc.support.RedirectAttributes; 
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import java.beans.PropertyEditorSupport;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Objects; // Importante para comparação segura

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
    public String salvar(@Valid @ModelAttribute("solicitacao") SolicitacaoTroca solicitacao, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        CadastroTreinador proponente = getTreinadorLogado();
        
        solicitacao.setTreinadorSolicitante(proponente); 
        if (solicitacao.getDataSolicitacao() == null) solicitacao.setDataSolicitacao(LocalDateTime.now()); 
        solicitacao.setStatusTroca(StatusTroca.PENDENTE); 

        // Validação: Verifica se o Pokémon já está em outra negociação PENDENTE
        if (solicitacao.getPokemonOferecido() != null) {
            boolean jaExiste = solicitacaoTrocaRepo.existsByPokemonOferecidoIdAndStatusTroca(
                solicitacao.getPokemonOferecido().getId(), StatusTroca.PENDENTE);
                
            if (jaExiste) {
                result.rejectValue("pokemonOferecido", "erro.duplicado", "Este Pokémon já está sendo oferecido em outra troca pendente.");
            }
        }

        if (result.hasErrors()) {
            prepararModelParaFormulario(model, proponente);
            return "Solicitacao/formulario-solicitacao";
        }
        
        solicitacaoTrocaRepo.save(solicitacao);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Solicitação enviada com sucesso!");
        
        return "redirect:/solicitacoes";
    }

    // --- MÉTODO CORRIGIDO PARA EVITAR O BUG ---
    @GetMapping("/{id}/aceitar")
    @Transactional
    public String aceitarTroca(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        SolicitacaoTroca solicitacao = solicitacaoTrocaRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Solicitação inválida"));
        
        // 1. Verifica se a solicitação ainda está PENDENTE
        if (solicitacao.getStatusTroca() != StatusTroca.PENDENTE) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Esta troca não está mais disponível.");
            return "redirect:/solicitacoes";
        }

        // Carrega os objetos atualizados do banco
        CadastroPokemon pokemonOferecido = solicitacao.getPokemonOferecido();
        CadastroPokemon pokemonSolicitado = solicitacao.getPokemonSolicitado(); // Pode ser null
        
        CadastroTreinador solicitanteOriginal = solicitacao.getTreinadorSolicitante();
        CadastroTreinador receptorAtual = solicitacao.getTreinadorReceptor(); // Quem está aceitando (Você)
        
        // 2. VALIDAÇÃO DE POSSE (CRÍTICO): O Solicitante ainda é dono do Pokémon oferecido?
        // Se ele já trocou esse Pokémon em outra solicitação, o ID do treinador será diferente.
        if (!Objects.equals(pokemonOferecido.getTreinador().getId(), solicitanteOriginal.getId())) {
            // Erro: O Pokémon já foi trocado!
            solicitacao.setStatusTroca(StatusTroca.RECUSADA); // Invalidamos essa solicitação automaticamente
            solicitacaoTrocaRepo.save(solicitacao);
            
            redirectAttributes.addFlashAttribute("mensagemErro", 
                "Troca falhou! O Pokémon " + pokemonOferecido.getNome() + " não pertence mais ao solicitante.");
            return "redirect:/solicitacoes";
        }

        // 3. VALIDAÇÃO DE POSSE: Se houver um Pokémon solicitado em troca, Você (Receptor) ainda o tem?
        if (pokemonSolicitado != null) {
            if (!Objects.equals(pokemonSolicitado.getTreinador().getId(), receptorAtual.getId())) {
                // Erro: Você não tem mais o Pokémon que o outro queria!
                redirectAttributes.addFlashAttribute("mensagemErro", 
                    "Troca falhou! Você não possui mais o Pokémon " + pokemonSolicitado.getNome() + " para dar em troca.");
                return "redirect:/solicitacoes";
            }
            
            // Se tiver tudo ok, transfere o seu Pokémon para o Solicitante
            pokemonSolicitado.setTreinador(solicitanteOriginal);
            pokemonRepo.save(pokemonSolicitado);
        }
            
        // 4. Transfere o Pokémon Oferecido para o Receptor (Você)
        pokemonOferecido.setTreinador(receptorAtual);
        pokemonRepo.save(pokemonOferecido);
        
        // 5. Finaliza
        solicitacao.setStatusTroca(StatusTroca.ACEITA);
        solicitacaoTrocaRepo.save(solicitacao);
        
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Troca realizada com sucesso!");
        
        return "redirect:/solicitacoes";
    }

    @GetMapping("/{id}/recusar")
    @Transactional
    public String recusarTroca(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        SolicitacaoTroca solicitacao = solicitacaoTrocaRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Solicitação inválida"));
            
        solicitacao.setStatusTroca(StatusTroca.RECUSADA);
        solicitacaoTrocaRepo.save(solicitacao);
        
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Solicitação recusada.");
        
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