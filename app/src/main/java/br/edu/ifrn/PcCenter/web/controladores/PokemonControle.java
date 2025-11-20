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
            .orElseThrow(() -> new IllegalStateException("Erro: Treinador logado não encontrado no banco de dados."));
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("nomeUsuario", getTreinadorLogado().getNome());
        model.addAttribute("pokemons", pokemonRepo.findAll());
        return "Pokemon/lista-pokemon";
    }

    @GetMapping("/novo")
    public String formulario(Model model) {
        model.addAttribute("nomeUsuario", getTreinadorLogado().getNome());
        model.addAttribute("pokemon", new CadastroPokemon());
        return "Pokemon/formulario-pokemon";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("pokemon") CadastroPokemon pokemon, BindingResult result) {
        
        // 1. Associa o Treinador ANTES da checagem de erros
        CadastroTreinador treinadorLogado = getTreinadorLogado();
        pokemon.setTreinador(treinadorLogado);

        // 2. Agora o erro do Treinador (se houver) é ignorado, e checamos os campos obrigatórios restantes
        if (result.hasErrors()) {
            // Se houver erro, recarregamos o nome do usuário para não perdê-lo
            result.getModel().put("nomeUsuario", treinadorLogado.getNome());
            return "Pokemon/formulario-pokemon";
        }
        
        pokemonRepo.save(pokemon);
        return "redirect:/pokemons";
    }
    
    // ... (restante do código)
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("nomeUsuario", getTreinadorLogado().getNome());
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