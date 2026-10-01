package br.gov.sp.etec.estacionamento.controller;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class PaginaAdvice {
    @ModelAttribute
    public void usuarioAtual(Authentication auth, Model model) {
        boolean conectado = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        model.addAttribute("conectado", conectado);
        model.addAttribute("admin", conectado && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        model.addAttribute("emailAtual", conectado ? auth.getName() : "");
    }
}
