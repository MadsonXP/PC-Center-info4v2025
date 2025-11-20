package br.edu.ifrn.PcCenter;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OlaSpringBoot {
    
    @GetMapping("/")
    public String index(){
        // CORREÇÃO FINAL: Retorna o template no caminho index/index.html
        return "index/index"; 
    }
}