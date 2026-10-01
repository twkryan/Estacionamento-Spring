package br.gov.sp.etec.estacionamento.controller;
import br.gov.sp.etec.estacionamento.entity.Papel;
import br.gov.sp.etec.estacionamento.model.Usuario;
import br.gov.sp.etec.estacionamento.service.UsuarioService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {
    private final UsuarioService usuarios;
    public LoginController(UsuarioService usuarios) { this.usuarios = usuarios; }
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("baseNova", usuarios.baseNova());
        model.addAttribute("migracao", usuarios.precisaMigrar());
        return "login";
    }
    @GetMapping("/cadastro")
    public String cadastro(Authentication auth, Model model) {
        exigirCadastroPermitido(auth);
        model.addAttribute("dados", new Usuario()); model.addAttribute("baseNova", usuarios.baseNova());
        return "cadastro";
    }
    @PostMapping("/efetuarCadastro")
    public String cadastrar(@ModelAttribute("dados") Usuario dados, org.springframework.validation.BindingResult binding,
                            @RequestParam(defaultValue="OPERADOR") Papel papel,
                            Authentication auth, Model model) {
        exigirCadastroPermitido(auth);
        try {
            if (binding.hasErrors()) throw new IllegalArgumentException("Informe uma data de nascimento válida.");
            usuarios.cadastrar(dados, papel, admin(auth));
            return admin(auth) ? "redirect:/usuarios?sucesso" : "redirect:/?cadastro";
        } catch (IllegalArgumentException ex) {
            dados.setInputSenhaCadastro(null);
            model.addAttribute("erro", ex.getMessage()); model.addAttribute("baseNova", usuarios.baseNova());
            return "cadastro";
        }
    }
    private boolean admin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
    private void exigirCadastroPermitido(Authentication auth) {
        if (!usuarios.baseNova() && !admin(auth)) throw new AccessDeniedException("Cadastro exige Admin.");
    }
}
