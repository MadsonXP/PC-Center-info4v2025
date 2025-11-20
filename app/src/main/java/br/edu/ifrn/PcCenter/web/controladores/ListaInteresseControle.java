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
        // REMOÇÃO LOGÍSTICA: Não enviamos mais a lista de treinadores para o formulário
        return "Interesse/formulario-interesse"; 
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("interesse") ListaInteresse interesse, BindingResult result) {
        if (result.hasErrors()) {
            return "Interesse/formulario-interesse";
        }

        // CORREÇÃO LOGÍSTICA: Simula o usuário logado (Treinador ID 1)
        Long treinadorLogadoId = 1L; 
        CadastroTreinador treinadorLogado = treinadorRepo.findById(treinadorLogadoId)
            .orElseThrow(() -> new IllegalStateException("Erro: Treinador logado (ID " + treinadorLogadoId + ") não encontrado!"));

        // Atribui o Treinador logado ao interesse
        interesse.setTreinador(treinadorLogado);

        listaInteresseRepo.save(interesse);
        return "redirect:/interesses";
    }
}