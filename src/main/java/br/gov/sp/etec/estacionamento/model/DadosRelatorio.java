package br.gov.sp.etec.estacionamento.model;

import java.util.Locale;

public record DadosRelatorio(ResultadoMovimentacoes movimentacoes, IndicadoresVagas indicadores) {
    public String getPercentualOcupacaoFormatado() {
        return String.format(Locale.ROOT, "%.1f%%", indicadores.percentualOcupacao());
    }
}
