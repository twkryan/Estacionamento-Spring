package br.gov.sp.etec.estacionamento.controller;

import br.gov.sp.etec.estacionamento.model.DadosRelatorio;
import br.gov.sp.etec.estacionamento.model.ResultadoMovimentacoes;
import br.gov.sp.etec.estacionamento.service.ConfiguracaoService;
import br.gov.sp.etec.estacionamento.service.RelatorioCsvService;
import br.gov.sp.etec.estacionamento.service.FiltroMovimentacoesInvalidoException;
import br.gov.sp.etec.estacionamento.service.FiltroMovimentacoesParser;
import br.gov.sp.etec.estacionamento.service.RelatorioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RelatorioController {
    private final FiltroMovimentacoesParser filtros;
    private final RelatorioService relatorios;
    private final ConfiguracaoService configuracoes;
    private final RelatorioCsvService csv;

    public RelatorioController(
            FiltroMovimentacoesParser filtros,
            RelatorioService relatorios,
            ConfiguracaoService configuracoes,
            RelatorioCsvService csv
    ) {
        this.filtros = filtros;
        this.relatorios = relatorios;
        this.configuracoes = configuracoes;
        this.csv = csv;
    }

    @GetMapping("/relatorios")
    public String consultar(
            @RequestParam(defaultValue = "") String placa,
            @RequestParam(defaultValue = "") String dataInicio,
            @RequestParam(defaultValue = "") String dataFim,
            Model model
    ) {
        model.addAttribute("placaFiltro", placa);
        model.addAttribute("dataInicioFiltro", dataInicio);
        model.addAttribute("dataFimFiltro", dataFim);
        model.addAttribute("exportacaoHabilitada", configuracoes.obter().exportacao());

        try {
            model.addAttribute("relatorio", relatorios.consultar(filtros.parsear(placa, dataInicio, dataFim)));
        } catch (FiltroMovimentacoesInvalidoException ex) {
            model.addAttribute("erro", ex.getMessage());
            model.addAttribute("relatorio", new DadosRelatorio(ResultadoMovimentacoes.vazio(),
                    configuracoes.indicadores()));
        }
        return "relatorios";
    }

    @GetMapping("/relatorios/exportar")
    public ResponseEntity<byte[]> exportar(
            @RequestParam(defaultValue = "") String placa,
            @RequestParam(defaultValue = "") String dataInicio,
            @RequestParam(defaultValue = "") String dataFim
    ) {
        if (!configuracoes.obter().exportacao()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            var filtro = filtros.parsear(placa, dataInicio, dataFim);
            var arquivo = csv.gerar(relatorios.consultar(filtro));
            return ResponseEntity.ok()
                    .contentType(new MediaType("text", "csv", java.nio.charset.StandardCharsets.UTF_8))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"relatorio-movimentacoes.csv\"")
                    .body(arquivo);
        } catch (FiltroMovimentacoesInvalidoException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
