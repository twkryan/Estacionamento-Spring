package br.gov.sp.etec.estacionamento.model;

public record ConfiguracaoAtual(int capacidade, boolean notificacoes, boolean backup,
                                boolean exportacao) {
}
