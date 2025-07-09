package br.edu.ifrn.PcCenter.web.controladores;

import br.edu.ifrn.PcCenter.persistencia.modelo.Treinador;
import br.edu.ifrn.PcCenter.persistencia.repositorio.TreinadorRepo;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("treinadores", treinadorRepo.findAll());
        return "Treinador/lista-treinador";
    }

    @GetMapping("/novo")
    public String formulario(Model model) {
        model.addAttribute("treinador", new Treinador());
        return "Treinador/formulario-treinador";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute Treinador treinador, BindingResult result) {
        if (treinadorRepo.findByEmail(treinador.getEmail()).isPresent()) {
            result.rejectValue("email", "erro.duplicado", "Já existe um treinador com este e-mail");
        }

        if (result.hasErrors()) {
            return "Treinador/formulario-treinador";
        }

        treinadorRepo.save(treinador);
        return "redirect:/treinadores";
    }
}
