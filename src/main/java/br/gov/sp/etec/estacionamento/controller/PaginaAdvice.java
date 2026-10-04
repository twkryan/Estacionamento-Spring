package br.gov.sp.etec.estacionamento.controller;

import br.gov.sp.etec.estacionamento.service.ConfiguracaoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class PaginaAdvice {
    private final ConfiguracaoService configuracoes;

    public PaginaAdvice(ConfiguracaoService configuracoes) {
        this.configuracoes = configuracoes;
    }

    @ModelAttribute
    public void usuarioAtual(Authentication auth, Model model, HttpServletRequest request) {
        if (request.getRequestURI().equals(request.getContextPath() + "/")) return;
        boolean conectado = auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser");
        model.addAttribute("conectado", conectado);
        model.addAttribute("admin", conectado && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        model.addAttribute("emailAtual", conectado ? auth.getName() : "");
        // A apresentação de erro não pode depender do serviço que pode ter causado a falha.
        if (request.getDispatcherType() == jakarta.servlet.DispatcherType.ERROR
                || request.getRequestURI().equals(request.getContextPath() + "/error")) return;
        model.addAttribute("notificacoes", configuracoes.obter().notificacoes());
    }
}
