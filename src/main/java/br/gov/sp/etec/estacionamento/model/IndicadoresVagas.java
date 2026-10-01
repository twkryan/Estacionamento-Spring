package br.gov.sp.etec.estacionamento.model;

public record IndicadoresVagas(int capacidade, long ocupacao, long vagasDisponiveis,
                               double percentualOcupacao) {
}
