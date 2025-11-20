package br.edu.ifrn.PcCenter.web.controladores;

import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroPokemon;
import br.edu.ifrn.PcCenter.persistencia.repositorio.PokemonRepo;
import br.edu.ifrn.PcCenter.persistencia.repositorio.TreinadorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/pokemons")
public class PokemonControle {

    @Autowired
    private PokemonRepo pokemonRepo;

    @Autowired
    private TreinadorRepo treinadorRepo;

    // GET /pokemons - Lista todos os Pokémons
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pokemons", pokemonRepo.findAll());
        return "Pokemon/lista-pokemon";
    }

    // GET /pokemons/novo - Exibe o formulário de cadastro
    @GetMapping("/novo")
    public String formulario(Model model) {
        model.addAttribute("pokemon", new CadastroPokemon());
        model.addAttribute("treinadores", treinadorRepo.findAll());
        return "Pokemon/formulario-pokemon";
    }

    // POST /pokemons - Salva (cadastro ou edição) o Pokémon
    @PostMapping
    public String salvar(@Valid @ModelAttribute("pokemon") CadastroPokemon pokemon, BindingResult result) {
        if (result.hasErrors()) {
            // Recarrega a lista de treinadores em caso de erro de validação
            result.getModel().put("treinadores", treinadorRepo.findAll());
            return "Pokemon/formulario-pokemon";
        }

        pokemonRepo.save(pokemon);
        return "redirect:/pokemons";
    }
    
    // GET /pokemons/{id}/editar - Exibe o formulário de edição (RF005)
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        CadastroPokemon pokemon = pokemonRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID de Pokémon inválido:" + id));
        
        model.addAttribute("pokemon", pokemon);
        model.addAttribute("treinadores", treinadorRepo.findAll());
        return "Pokemon/formulario-pokemon";
    }

    // GET /pokemons/{id}/excluir - Deleta o Pokémon (RF003)
    @GetMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        pokemonRepo.deleteById(id);
        return "redirect:/pokemons";
    }
}