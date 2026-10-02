package br.gov.sp.etec.estacionamento.model;

import java.util.Locale;

public final class Placa {
    private Placa() {
    }

    public static String normalizar(String placa) {
        if (placa == null) return "";
        return placa.toUpperCase(Locale.ROOT).replaceAll("[\\s\\p{Z}-]", "");
    }
}
