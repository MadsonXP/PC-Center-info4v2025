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

import java.util.Collections;
import java.util.List; 

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

    // MÉTODO ATUALIZADO: Separa Pokémons em duas listas
    @GetMapping
    public String listar(Model model) {
        CadastroTreinador treinadorLogado = getTreinadorLogado();
        Long treinadorId = treinadorLogado.getId();
        
        // 1. Busca os Pokémons do próprio Treinador
        List<CadastroPokemon> meusPokemons = pokemonRepo.findByTreinadorId(treinadorId);
        // 2. Busca os Pokémons de outros Treinadores
        List<CadastroPokemon> outrosPokemons = pokemonRepo.findByTreinadorIdNot(treinadorId);
        
        model.addAttribute("nomeUsuario", treinadorLogado.getNome());
        model.addAttribute("meusPokemons", meusPokemons);
        model.addAttribute("outrosPokemons", outrosPokemons);
        
        return "Pokemon/lista-pokemon";
    }

    @GetMapping("/novo")
    public String formulario(Model model) {
        CadastroTreinador treinadorLogado = getTreinadorLogado();
        
        model.addAttribute("nomeUsuario", treinadorLogado.getNome());
        model.addAttribute("pokemon", new CadastroPokemon());
        
        // Envia apenas o treinador logado para o binding no formulário (campo oculto)
        model.addAttribute("treinadores", Collections.singletonList(treinadorLogado));
        
        return "Pokemon/formulario-pokemon";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("pokemon") CadastroPokemon pokemon, BindingResult result) {
        
        if (result.hasErrors()) {
            CadastroTreinador treinadorLogado = getTreinadorLogado();
            // Recarrega o nome e a lista de treinadores em caso de erro de validação
            result.getModel().put("nomeUsuario", treinadorLogado.getNome());
            result.getModel().put("treinadores", Collections.singletonList(treinadorLogado));
            return "Pokemon/formulario-pokemon";
        }
        
        pokemonRepo.save(pokemon);
        return "redirect:/pokemons";
    }
    
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("nomeUsuario", getTreinadorLogado().getNome());
        CadastroPokemon pokemon = pokemonRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID de Pokémon inválido:" + id));
        
        model.addAttribute("pokemon", pokemon);
        // Em edição, pode-se listar todos os treinadores (visão de administrador/proprietário)
        model.addAttribute("treinadores", treinadorRepo.findAll()); 
        return "Pokemon/formulario-pokemon";
    }

    @GetMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        // NOTA DE SEGURANÇA: Aqui deveria haver uma verificação 
        // se o Pokémon pertence ao usuário logado. 
        pokemonRepo.deleteById(id);
        return "redirect:/pokemons";
    }
}