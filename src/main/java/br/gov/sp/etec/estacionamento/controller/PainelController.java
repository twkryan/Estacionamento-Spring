package br.gov.sp.etec.estacionamento.controller;

import br.gov.sp.etec.estacionamento.service.ConfiguracaoService;
import br.gov.sp.etec.estacionamento.service.VeiculoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PainelController {
    private final ConfiguracaoService configuracaoService;
    private final VeiculoService veiculoService;

    public PainelController(ConfiguracaoService configuracaoService, VeiculoService veiculoService) {
        this.configuracaoService = configuracaoService;
        this.veiculoService = veiculoService;
    }

    @GetMapping("/painel")
    public String painel(Model model) {
        model.addAttribute("indicadores", configuracaoService.indicadores());
        model.addAttribute("entradas", veiculoService.listarEntradasAbertas(""));
        return "painel";
    }
}
