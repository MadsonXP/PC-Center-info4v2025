package br.edu.ifrn.PcCenter.web.controladores;

import br.edu.ifrn.PcCenter.persistencia.modelo.SolicitacaoTroca; 
import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroTreinador;
import br.edu.ifrn.PcCenter.persistencia.modelo.ListaInteresse;
import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroPokemon;
import br.edu.ifrn.PcCenter.persistencia.modelo.StatusTroca; // Import do Enum
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

import java.time.LocalDateTime; // Data/Hora (CORRIGIDO: Usamos LocalDateTime)
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/solicitacoes")
public class SolicitacaoTrocaControle {

    @Autowired
    private SolicitacaoTrocaRepo solicitacaoTrocaRepo;

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

    @GetMapping
    public String listar(Model model) {
        CadastroTreinador treinadorLogado = getTreinadorLogado();
        Long treinadorId = treinadorLogado.getId();
        
        // CORRIGIDO: O nome do método no Repositório é findByTreinadorSolicitanteId
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
        
        model.addAttribute("nomeUsuario", proponente.getNome());
        model.addAttribute("solicitacao", solicitacao);
        
        // Prepara os dados para o formulário
        model.addAttribute("proponente", proponente);
        model.addAttribute("meusPokemons", pokemonRepo.findByTreinadorId(proponente.getId()));
        
        if (interesseId != null) {
            Optional<ListaInteresse> interesseOpt = listaInteresseRepo.findById(interesseId);
            if (interesseOpt.isPresent()) {
                ListaInteresse interesse = interesseOpt.get();
                
                // NOTA: Para compilar, removemos a lógica complexa de setar o Pokemon solicitado (CadastroPokemon) 
                // a partir de um objeto ListaInteresse. Essa lógica precisa ser refeita no formulário.
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
        
        // CORRIGIDO: Usando o nome do método correto e a classe correta
        solicitacao.setTreinadorSolicitante(proponente); 
        
        // CORRIGIDO: Usando LocalDateTime (para ser compatível com DATETIME no SQL)
        solicitacao.setDataSolicitacao(LocalDateTime.now()); 
        
        // CORRIGIDO: Usando o Enum StatusTroca
        solicitacao.setStatusTroca(br.edu.ifrn.PcCenter.persistencia.modelo.StatusTroca.PENDENTE); 

        // CRÍTICO: Se a validação falhar, o Proponente e as listas são perdidos, precisamos re-injetá-los
        if (result.hasErrors()) {
            solicitacao.setTreinadorSolicitante(proponente);
            result.getModel().put("nomeUsuario", proponente.getNome());
            result.getModel().put("proponente", proponente);
            result.getModel().put("meusPokemons", pokemonRepo.findByTreinadorId(proponente.getId()));
            
            // Recarrega as listas de interesse/receptor para evitar falhas no HTML
            if (solicitacao.getPokemonSolicitado() != null) {
                 // Acesso corrigido ao getter getPokemonSolicitado
                 result.getModel().put("interesseSelecionado", solicitacao.getPokemonSolicitado());
                 result.getModel().put("receptorSelecionado", solicitacao.getTreinadorReceptor());
            } else {
                 result.getModel().put("todosInteresses", listaInteresseRepo.findAll());
            }
            
            return "Solicitacao/formulario-solicitacao";
        }
        
        solicitacaoTrocaRepo.save(solicitacao);
        return "redirect:/solicitacoes";
    }
}