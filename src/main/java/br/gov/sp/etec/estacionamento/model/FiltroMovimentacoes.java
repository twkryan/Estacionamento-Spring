package br.gov.sp.etec.estacionamento.model;

import java.time.LocalDate;

public record FiltroMovimentacoes(String placa, LocalDate dataInicio, LocalDate dataFim) {
}
