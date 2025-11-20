package br.edu.ifrn.PcCenter.web.controladores;

import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroPokemon;
import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroTreinador;
import br.edu.ifrn.PcCenter.persistencia.repositorio.PokemonRepo;
import br.edu.ifrn.PcCenter.persistencia.repositorio.TreinadorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Collections; // Import necessário
import java.util.Optional; // Import necessário

@Controller
@RequestMapping("/pokemons")
public class PokemonControle {

    @Autowired
    private PokemonRepo pokemonRepo;

    @Autowired
    private TreinadorRepo treinadorRepo;

    // Método auxiliar para buscar o Treinador logado
    private CadastroTreinador getTreinadorLogado() {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        return treinadorRepo.findByEmail(emailLogado) 
            .orElseThrow(() -> new IllegalStateException("Erro: Treinador logado (" + emailLogado + ") não encontrado no banco de dados."));
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("nomeUsuario", getTreinadorLogado().getNome());
        model.addAttribute("pokemons", pokemonRepo.findAll());
        return "Pokemon/lista-pokemon";
    }

    @GetMapping("/novo")
    public String formulario(Model model) {
        CadastroTreinador treinadorLogado = getTreinadorLogado();
        
        // 1. OBTÉM O OBJETO E NOME
        model.addAttribute("nomeUsuario", treinadorLogado.getNome());
        model.addAttribute("pokemon", new CadastroPokemon());
        
        // 2. CORREÇÃO CRÍTICA: Envia UMA LISTA contendo APENAS o usuário logado para o formulário
        model.addAttribute("treinadores", Collections.singletonList(treinadorLogado));
        
        return "Pokemon/formulario-pokemon";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("pokemon") CadastroPokemon pokemon, BindingResult result) {
        
        // CORREÇÃO CRÍTICA: Se a validação falhar, precisamos re-injetar o nome e a lista de treinadores
        if (result.hasErrors()) {
            CadastroTreinador treinadorLogado = getTreinadorLogado();
            result.getModel().put("nomeUsuario", treinadorLogado.getNome());
            result.getModel().put("treinadores", Collections.singletonList(treinadorLogado));
            return "Pokemon/formulario-pokemon";
        }
        
        // A lógica de associação (pokemon.setTreinador(treinadorLogado)) foi removida daqui, 
        // pois o Thymeleaf a fará automaticamente via binding (th:field="*{treinador}")
        
        pokemonRepo.save(pokemon);
        return "redirect:/pokemons";
    }
    
    // ... (Métodos editar e excluir permanecem os mesmos) ...
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("nomeUsuario", getTreinadorLogado().getNome());
        CadastroPokemon pokemon = pokemonRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID de Pokémon inválido:" + id));
        
        model.addAttribute("pokemon", pokemon);
        // CRÍTICA: Enviamos todos os treinadores para a edição (visão de admin)
        model.addAttribute("treinadores", treinadorRepo.findAll()); 
        return "Pokemon/formulario-pokemon";
    }

    @GetMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        pokemonRepo.deleteById(id);
        return "redirect:/pokemons";
    }
}