package br.gov.sp.etec.estacionamento.model;

import java.util.List;

public record ResultadoMovimentacoes(
        List<MovimentacaoDTO> movimentacoes,
        long total,
        Long mediaPermanenciaMinutos
) {
    public ResultadoMovimentacoes {
        movimentacoes = List.copyOf(movimentacoes);
    }

    public String getMediaPermanenciaFormatada() {
        return mediaPermanenciaMinutos == null
                ? null
                : MovimentacaoDTO.formatarPermanencia(mediaPermanenciaMinutos);
    }

    public static ResultadoMovimentacoes vazio() {
        return new ResultadoMovimentacoes(List.of(), 0, null);
    }
}
