package br.gov.sp.etec.estacionamento.controller;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ErroController {
    @GetMapping("/acesso-negado")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String acessoNegado(Model model) {
        model.addAttribute("erro", "Seu usuário não tem permissão para esta ação.");
        return "acesso-negado";
    }
}
