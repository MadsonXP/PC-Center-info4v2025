package br.edu.ifrn.PcCenter.web.controladores;

import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroPokemon;
import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroTreinador; // Import necessário
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

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pokemons", pokemonRepo.findAll());
        return "Pokemon/lista-pokemon";
    }

    @GetMapping("/novo")
    public String formulario(Model model) {
        model.addAttribute("pokemon", new CadastroPokemon());
        // REMOÇÃO LOGÍSTICA: Não precisamos listar treinadores
        return "Pokemon/formulario-pokemon";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("pokemon") CadastroPokemon pokemon, BindingResult result) {
        
        // CORREÇÃO LOGÍSTICA/VALIDAÇÃO: Associa o Treinador ID 1 antes de checar erros
        Long treinadorLogadoId = 1L; 
        CadastroTreinador treinadorLogado = treinadorRepo.findById(treinadorLogadoId)
            .orElseThrow(() -> new IllegalStateException("Erro: Treinador logado (ID " + treinadorLogadoId + ") não encontrado!"));

        pokemon.setTreinador(treinadorLogado);
        
        if (result.hasErrors()) {
            return "Pokemon/formulario-pokemon";
        }
        
        pokemonRepo.save(pokemon);
        return "redirect:/pokemons";
    }
    
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        CadastroPokemon pokemon = pokemonRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID de Pokémon inválido:" + id));
        
        model.addAttribute("pokemon", pokemon);
        // REMOÇÃO LOGÍSTICA: Não precisamos listar treinadores
        return "Pokemon/formulario-pokemon";
    }

    @GetMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        pokemonRepo.deleteById(id);
        return "redirect:/pokemons";
    }
}