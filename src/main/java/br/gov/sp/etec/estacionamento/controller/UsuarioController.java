package br.gov.sp.etec.estacionamento.controller;
import br.gov.sp.etec.estacionamento.entity.Papel;
import br.gov.sp.etec.estacionamento.model.Usuario;
import br.gov.sp.etec.estacionamento.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarios;
    public UsuarioController(UsuarioService usuarios) { this.usuarios = usuarios; }
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarios.listar()); return "usuarios";
    }
    @GetMapping("/{id}")
    public String editar(@PathVariable Long id, Model model) {
        var usuario = usuarios.buscar(id);
        var dados = new Usuario();
        dados.setInputNomeCadastro(usuario.getInputNomeCadastro()); dados.setInputEmailCadastro(usuario.getInputEmailCadastro());
        dados.setInputCPFCadastro(usuario.getInputCPFCadastro()); dados.setInputTelefone(usuario.getInputTelefone());
        dados.setInputDataNascimentoCadastro(usuario.getInputDataNascimentoCadastro());
        model.addAttribute("dados", dados); model.addAttribute("id", id);
        model.addAttribute("papel", usuario.getPapel()); model.addAttribute("ativo", usuario.isAtivo());
        return "usuario-editar";
    }
    @PostMapping("/{id}")
    public String salvar(@PathVariable Long id, @ModelAttribute("dados") Usuario dados,
                         org.springframework.validation.BindingResult binding, @RequestParam Papel papel,
                         @RequestParam(defaultValue="false") boolean ativo, Model model) {
        try {
            if (binding.hasErrors()) throw new IllegalArgumentException("Informe uma data de nascimento válida.");
            usuarios.atualizar(id, dados, papel, ativo); return "redirect:/usuarios?sucesso";
        } catch (IllegalArgumentException ex) {
            dados.setInputSenhaCadastro(null);
            model.addAttribute("id", id); model.addAttribute("papel", papel); model.addAttribute("ativo", ativo);
            model.addAttribute("erro", ex.getMessage()); return "usuario-editar";
        }
    }
}
