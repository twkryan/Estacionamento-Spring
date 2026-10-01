package br.gov.sp.etec.estacionamento.controller;

import br.gov.sp.etec.estacionamento.model.Veiculo;
import br.gov.sp.etec.estacionamento.service.UsuarioService;
import br.gov.sp.etec.estacionamento.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("veiculo")
public class PainelVeiculoController {

    @Autowired
    VeiculoService service;

    @PostMapping("cadastrar")
    public String cadastrarVeiculo(Veiculo veiculo) {
        service.cadastrarVeiculo(veiculo);
        return "painel";
    }

    @GetMapping("registrar-entrada")
    public String registrarEntrada() {
        return "registrar-entrada";
    }

}
