package br.gov.sp.etec.estacionamento.service;

import br.gov.sp.etec.estacionamento.model.DadosRelatorio;
import br.gov.sp.etec.estacionamento.model.MovimentacaoDTO;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

@Service
public class RelatorioCsvService {
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Pattern FORMULA_DE_PLANILHA = Pattern.compile("(?s)^[\\p{Z}\\s\\p{Cc}\\p{Cf}]*[=+\\-@].*");

    public byte[] gerar(DadosRelatorio relatorio) {
        StringBuilder csv = new StringBuilder("\uFEFF");
        linha(csv, "Placa", "Modelo", "Entrada", "Saída", "Situação", "Permanência");
        for (MovimentacaoDTO movimentacao : relatorio.movimentacoes().movimentacoes()) {
            linha(csv,
                    movimentacao.placa(),
                    movimentacao.modelo(),
                    formatarDataHora(movimentacao.horaEntrada()),
                    formatarDataHora(movimentacao.horaSaida()),
                    movimentacao.aberta() ? "Aberta" : "Encerrada",
                    movimentacao.getPermanenciaFormatada());
        }

        linha(csv, "", "", "", "", "", "");
        linha(csv, "Resumo", "", "", "", "", "");
        linha(csv, "Total de movimentações", Long.toString(relatorio.movimentacoes().total()), "", "", "", "");
        String media = relatorio.movimentacoes().mediaPermanenciaMinutos() == null
                ? "Não há visitas encerradas para calcular a média."
                : relatorio.movimentacoes().getMediaPermanenciaFormatada();
        linha(csv, "Média de permanência", media, "", "", "", "");
        linha(csv, "Capacidade", Integer.toString(relatorio.indicadores().capacidade()), "", "", "", "");
        linha(csv, "Ocupação atual", Long.toString(relatorio.indicadores().ocupacao()), "", "", "", "");
        linha(csv, "Vagas disponíveis", Long.toString(relatorio.indicadores().vagasDisponiveis()), "", "", "", "");
        linha(csv, "Percentual de ocupação", relatorio.getPercentualOcupacaoFormatado(), "", "", "", "");

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void linha(StringBuilder csv, String... valores) {
        for (int i = 0; i < valores.length; i++) {
            if (i > 0) csv.append(';');
            String valor = protegerFormula(valores[i]);
            csv.append('"').append(valor.replace("\"", "\"\"")).append('"');
        }
        csv.append("\r\n");
    }

    private String protegerFormula(String valor) {
        if (valor == null) return "";
        return FORMULA_DE_PLANILHA.matcher(valor).matches() ? "'" + valor : valor;
    }

    private String formatarDataHora(java.time.LocalDateTime dataHora) {
        return dataHora == null ? "—" : DATA_HORA.format(dataHora);
    }
}
