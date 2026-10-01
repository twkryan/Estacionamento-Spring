package br.gov.sp.etec.estacionamento.controller;

import br.gov.sp.etec.estacionamento.model.FiltroMovimentacoes;
import br.gov.sp.etec.estacionamento.model.ResultadoMovimentacoes;
import br.gov.sp.etec.estacionamento.service.MovimentacaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Controller
public class MovimentacaoController {
    private final MovimentacaoService movimentacoes;

    public MovimentacaoController(MovimentacaoService movimentacoes) {
        this.movimentacoes = movimentacoes;
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
            LocalDate inicio = parsearData(dataInicio);
            LocalDate fim = parsearData(dataFim);
            if (inicio != null && fim != null && inicio.isAfter(fim)) {
                throw new PeriodoMovimentacoesInvalidoException();
            }
            model.addAttribute("resultado", movimentacoes.consultar(new FiltroMovimentacoes(placa, inicio, fim)));
        } catch (DateTimeParseException ex) {
            model.addAttribute("erro", "Informe datas de entrada válidas.");
            model.addAttribute("resultado", ResultadoMovimentacoes.vazio());
        } catch (PeriodoMovimentacoesInvalidoException ex) {
            model.addAttribute("erro", "A data inicial não pode ser posterior à data final.");
            model.addAttribute("resultado", ResultadoMovimentacoes.vazio());
        }
        return "movimentacoes";
    }

    private LocalDate parsearData(String data) {
        return data == null || data.isBlank() ? null : LocalDate.parse(data);
    }

    private static class PeriodoMovimentacoesInvalidoException extends RuntimeException {
    }
}
