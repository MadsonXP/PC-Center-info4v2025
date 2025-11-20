package br.edu.ifrn.PcCenter.web.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginControle {
    
    // Mapeia a URL /login para o caminho do template: templates/login/login.html
    @GetMapping("/login")
    public String login() {
        return "login/login";
    }
}