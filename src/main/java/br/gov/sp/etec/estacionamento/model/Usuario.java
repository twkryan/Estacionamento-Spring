package br.gov.sp.etec.estacionamento.model;

import java.time.LocalDate;

public class Usuario {
    private String inputNomeCadastro;
    private String inputCPFCadastro;
    private String inputEmailCadastro;
    private String inputSenhaCadastro;
    @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE)
    private LocalDate inputDataNascimentoCadastro;
    private String inputTelefone;

    public String getInputNomeCadastro() {
        return inputNomeCadastro;
    }

    public void setInputNomeCadastro(String inputNomeCadastro) {
        this.inputNomeCadastro = inputNomeCadastro;
    }

    public String getInputCPFCadastro() {
        return inputCPFCadastro;
    }

    public void setInputCPFCadastro(String inputCPFCadastro) {
        this.inputCPFCadastro = inputCPFCadastro;
    }

    public String getInputEmailCadastro() {
        return inputEmailCadastro;
    }

    public void setInputEmailCadastro(String inputEmailCadastro) {
        this.inputEmailCadastro = inputEmailCadastro;
    }

    public String getInputSenhaCadastro() {
        return inputSenhaCadastro;
    }

    public void setInputSenhaCadastro(String inputSenhaCadastro) {
        this.inputSenhaCadastro = inputSenhaCadastro;
    }

    public LocalDate getInputDataNascimentoCadastro() {
        return inputDataNascimentoCadastro;
    }

    public void setInputDataNascimentoCadastro(LocalDate inputDataNascimentoCadastro) {
        this.inputDataNascimentoCadastro = inputDataNascimentoCadastro;
    }

    public String getInputTelefone() {
        return inputTelefone;
    }

    public void setInputTelefone(String inputTelefone) {
        this.inputTelefone = inputTelefone;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "inputNomeCadastro='" + inputNomeCadastro + '\'' +
                ", inputCPFCadastro=" + inputCPFCadastro +
                ", inputEmailCadastro='" + inputEmailCadastro + '\'' +
                ", inputDataNascimentoCadastro=" + inputDataNascimentoCadastro +
                ", inputTelefone=" + inputTelefone +
                '}';
    }
}
