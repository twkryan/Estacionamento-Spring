package br.gov.sp.etec.estacionamento.service;

public class FiltroMovimentacoesInvalidoException extends RuntimeException {
    public FiltroMovimentacoesInvalidoException(String mensagem) {
        super(mensagem);
    }
}
