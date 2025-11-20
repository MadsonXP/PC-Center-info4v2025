package br.edu.ifrn.PcCenter.web.controladores;

import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroTreinador;
import br.edu.ifrn.PcCenter.persistencia.repositorio.TreinadorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.time.LocalDate; 
import java.util.Optional; 

@Controller
@RequestMapping("/treinadores")
public class TreinadorControle {

    @Autowired
    private TreinadorRepo treinadorRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder; 

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("treinadores", treinadorRepo.findAll());
        return "Treinador/lista-treinador";
    }

    @GetMapping("/novo")
    public String formulario(Model model) {
        model.addAttribute("treinador", new CadastroTreinador());
        return "Treinador/formulario-treinador";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("treinador") CadastroTreinador treinador, BindingResult result) {
        // Verifica se o e-mail existe, mas permite se for o mesmo usuário (edição)
        Optional<CadastroTreinador> existente = treinadorRepo.findByEmail(treinador.getEmail());
        
        if (existente.isPresent() && (treinador.getId() == null || !existente.get().getId().equals(treinador.getId()))) {
            result.rejectValue("email", "erro.duplicado", "Já existe um treinador com este e-mail");
        }

        if (result.hasErrors()) {
            return "Treinador/formulario-treinador";
        }
        
        // Define a data de cadastro automaticamente se for novo
        if (treinador.getDataCadastro() == null) {
            treinador.setDataCadastro(LocalDate.now());
        }

        // Codifica a senha antes de salvar
        String senhaPura = treinador.getSenha();
        String senhaCodificada = passwordEncoder.encode(senhaPura);
        treinador.setSenha(senhaCodificada);
        
        treinadorRepo.save(treinador);
        
        return "redirect:/login"; 
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        CadastroTreinador treinador = treinadorRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID de Treinador inválido:" + id));
        
        model.addAttribute("treinador", treinador);
        return "Treinador/formulario-treinador";
    }

    @GetMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        treinadorRepo.deleteById(id);
        return "redirect:/treinadores";
    }
}