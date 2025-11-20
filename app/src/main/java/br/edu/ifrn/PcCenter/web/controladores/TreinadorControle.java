package br.edu.ifrn.PcCenter.web.controladores;

import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroTreinador;
import br.edu.ifrn.PcCenter.persistencia.repositorio.TreinadorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // NOVO: Importa o PasswordEncoder
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/treinadores")
public class TreinadorControle {

    @Autowired
    private TreinadorRepo treinadorRepo;
    
    // NOVO: Injeta o codificador de senhas (BCrypt)
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
        // Lógica de verificação de e-mail duplicado
        if (treinadorRepo.findByEmail(treinador.getEmail()).isPresent()) {
            result.rejectValue("email", "erro.duplicado", "Já existe um treinador com este e-mail");
        }
        
        if (result.hasErrors()) {
            return "Treinador/formulario-treinador";
        }

        // CORREÇÃO ESSENCIAL: Codifica a senha antes de salvar no banco
        String senhaPura = treinador.getSenha();
        String senhaCodificada = passwordEncoder.encode(senhaPura);
        treinador.setSenha(senhaCodificada);
        
        treinadorRepo.save(treinador);
        return "redirect:/treinadores"; // Redireciona para a lista
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        CadastroTreinador treinador = treinadorRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("ID de Treinador inválido:" + id));
        
        // Limpamos a senha para que ela não seja exibida, mas o campo de senha
        // deve ser preenchido caso o usuário queira alterá-la.
        treinador.setSenha(null); 
        
        model.addAttribute("treinador", treinador);
        return "Treinador/formulario-treinador";
    }

    @GetMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        treinadorRepo.deleteById(id);
        return "redirect:/treinadores";
    }
}