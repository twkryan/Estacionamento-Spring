package br.gov.sp.etec.estacionamento.controller;

import br.gov.sp.etec.estacionamento.model.Usuario;
import br.gov.sp.etec.estacionamento.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UsuarioService service;

    @GetMapping("/")
    public String index() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastro() {
        return "cadastro";
    }

    @PostMapping("/efetuarCadastro")
    public String efetuarCadastro(Usuario usuario){
        log.info(usuario.toString());
        service.cadastrarUsuario(usuario);
        return "login";
    }
    @PostMapping ("/autenticar")
    public String autenticar(String inputEmail, String inputSenha){
        Usuario user = service.buscaUsuarioPorEmail(inputEmail);
        if (user != null && inputSenha.equals(user.getInputSenhaCadastro())){
            return "painel";
        }
        else {
            return "erro";
        }
    }
}


