package br.edu.ifrn.PcCenter.web.controladores;

import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroPokemon;
import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroTreinador;
import br.edu.ifrn.PcCenter.persistencia.repositorio.PokemonRepo;
import br.edu.ifrn.PcCenter.persistencia.repositorio.TreinadorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder; // <<-- IMPORT ESSENCIAL
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
        return "Pokemon/formulario-pokemon";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("pokemon") CadastroPokemon pokemon, BindingResult result) {
        
        // CORREÇÃO CRÍTICA: Lógica de Associação do Treinador Logado
        
        // 1. Obtém o e-mail do usuário autenticado
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // 2. Busca o objeto Treinador completo pelo e-mail
        CadastroTreinador treinadorLogado = treinadorRepo.findByEmail(emailLogado)
            .orElseThrow(() -> new IllegalStateException("Erro: Treinador logado com o e-mail " + emailLogado + " não foi encontrado no banco de dados!"));

        // 3. Define o Treinador do Pokémon, satisfazendo o requisito @NotNull
        pokemon.setTreinador(treinadorLogado);

        // 4. Checa as demais validações (se falhar, o erro é nos campos do Pokémon, não no Treinador)
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
        return "Pokemon/formulario-pokemon";
    }

    @GetMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        pokemonRepo.deleteById(id);
        return "redirect:/pokemons";
    }
}