package br.gov.sp.etec.estacionamento.model;

import java.time.Duration;
import java.time.LocalDateTime;

public record MovimentacaoDTO(
        Long id,
        String placa,
        String modelo,
        LocalDateTime horaEntrada,
        LocalDateTime horaSaida,
        boolean aberta,
        long permanenciaMinutos
) {
    public String getPermanenciaFormatada() {
        return formatarPermanencia(permanenciaMinutos);
    }

    public static String formatarPermanencia(long minutos) {
        long minutosNaoNegativos = Math.max(0, minutos);
        return "%dh %02dmin".formatted(minutosNaoNegativos / 60, minutosNaoNegativos % 60);
    }

    public static long calcularPermanencia(LocalDateTime entrada, LocalDateTime fim) {
        if (entrada == null || fim == null) return 0;
        return Math.max(0, Duration.between(entrada, fim).toMinutes());
    }
}
