package br.edu.ifrn.PcCenter.web.controladores;

import br.edu.ifrn.PcCenter.persistencia.modelo.ListaInteresse;
import br.edu.ifrn.PcCenter.persistencia.modelo.CadastroTreinador;
import br.edu.ifrn.PcCenter.persistencia.repositorio.ListaInteresseRepo;
import br.edu.ifrn.PcCenter.persistencia.repositorio.TreinadorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/interesses")
public class ListaInteresseControle {

    @Autowired
    private ListaInteresseRepo listaInteresseRepo;

    @Autowired
    private TreinadorRepo treinadorRepo; 

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("interesses", listaInteresseRepo.findAll());
        return "Interesse/lista-interesse"; 
    }

    @GetMapping("/novo")
    public String formulario(Model model) {
        model.addAttribute("interesse", new ListaInteresse());
        // RESTAURADO: Enviamos todos os treinadores para o dropdown
        model.addAttribute("treinadores", treinadorRepo.findAll()); 
        return "Interesse/formulario-interesse"; 
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("interesse") ListaInteresse interesse, BindingResult result) {
        if (result.hasErrors()) {
            // Se houver erro, recarregamos a lista de treinadores
            result.getModel().put("treinadores", treinadorRepo.findAll());
            return "Interesse/formulario-interesse";
        }
        
        // REMOVIDO: A lógica de forçar o Treinador ID 1

        listaInteresseRepo.save(interesse);
        return "redirect:/interesses";
    }
}