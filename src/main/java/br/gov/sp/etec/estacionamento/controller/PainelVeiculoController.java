package br.gov.sp.etec.estacionamento.controller;

import br.gov.sp.etec.estacionamento.model.Veiculo;
import br.gov.sp.etec.estacionamento.service.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("registrar-saida")
    public String registrarSaida(@RequestParam(defaultValue = "") String placa, Model model) {
        model.addAttribute("placa", placa.trim());
        model.addAttribute("entradas", service.listarEntradasAbertas(placa));
        model.addAttribute("historico", service.listarHistoricoSaidas(placa));
        return "registrar-saida";
    }

    @PostMapping("registrar-saida")
    public String confirmarSaida(@RequestParam Long id, RedirectAttributes redirect) {
        if (service.registrarSaida(id)) {
            redirect.addFlashAttribute("mensagem", "Saída registrada com sucesso.");
        } else {
            redirect.addFlashAttribute("erro", "Entrada não encontrada ou já encerrada. Nenhum registro foi alterado.");
        }
        return "redirect:/veiculo/registrar-saida";
    }

}
