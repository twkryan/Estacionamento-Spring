package br.gov.sp.etec.estacionamento.service;

import java.util.Locale;

final class PlacaMovimentacoes {
    private PlacaMovimentacoes() {
    }

    static String normalizar(String placa) {
        if (placa == null || placa.isBlank()) return null;
        String normalizada = placa.trim().toUpperCase(Locale.ROOT)
                .replace(" ", "")
                .replace("-", "");
        return normalizada.isEmpty() ? null : normalizada;
    }
}
