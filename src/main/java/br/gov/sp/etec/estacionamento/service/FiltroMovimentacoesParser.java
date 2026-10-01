package br.gov.sp.etec.estacionamento.service;

import br.gov.sp.etec.estacionamento.model.FiltroMovimentacoes;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class FiltroMovimentacoesParser {
    public FiltroMovimentacoes parsear(String placa, String dataInicio, String dataFim) {
        LocalDate inicio;
        LocalDate fim;
        try {
            inicio = parsearData(dataInicio);
            fim = parsearData(dataFim);
        } catch (DateTimeParseException ex) {
            throw new FiltroMovimentacoesInvalidoException("Informe datas de entrada válidas.");
        }

        if (inicio != null && fim != null && inicio.isAfter(fim)) {
            throw new FiltroMovimentacoesInvalidoException("A data inicial não pode ser posterior à data final.");
        }
        return new FiltroMovimentacoes(placa, inicio, fim);
    }

    private LocalDate parsearData(String data) {
        return data == null || data.isBlank() ? null : LocalDate.parse(data);
    }
}
