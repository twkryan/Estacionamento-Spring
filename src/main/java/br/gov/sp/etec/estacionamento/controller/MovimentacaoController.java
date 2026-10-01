package br.gov.sp.etec.estacionamento.controller;

import br.gov.sp.etec.estacionamento.model.ResultadoMovimentacoes;
import br.gov.sp.etec.estacionamento.service.FiltroMovimentacoesInvalidoException;
import br.gov.sp.etec.estacionamento.service.FiltroMovimentacoesParser;
import br.gov.sp.etec.estacionamento.service.MovimentacaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MovimentacaoController {
    private final MovimentacaoService movimentacoes;
    private final FiltroMovimentacoesParser filtros;

    public MovimentacaoController(MovimentacaoService movimentacoes, FiltroMovimentacoesParser filtros) {
        this.movimentacoes = movimentacoes;
        this.filtros = filtros;
    }

    @GetMapping("/movimentacoes")
    public String listar(
            @RequestParam(defaultValue = "") String placa,
            @RequestParam(defaultValue = "") String dataInicio,
            @RequestParam(defaultValue = "") String dataFim,
            Model model
    ) {
        model.addAttribute("placaFiltro", placa);
        model.addAttribute("dataInicioFiltro", dataInicio);
        model.addAttribute("dataFimFiltro", dataFim);

        try {
            model.addAttribute("resultado", movimentacoes.consultar(filtros.parsear(placa, dataInicio, dataFim)));
        } catch (FiltroMovimentacoesInvalidoException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("resultado", ResultadoMovimentacoes.vazio());
        }
        return "movimentacoes";
    }
}
