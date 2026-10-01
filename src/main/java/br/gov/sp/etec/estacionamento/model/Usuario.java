package br.gov.sp.etec.estacionamento.model;

import java.time.LocalDate;

public class Usuario {
    private String inputNomeCadastro;
    private Integer inputCPFCadastro;
    private String inputEmailCadastro;
    private String inputSenhaCadastro;
    private LocalDate inputDataNascimentoCadastro;
    private Integer inputTelefone;

    public String getInputNomeCadastro() {
        return inputNomeCadastro;
    }

    public void setInputNomeCadastro(String inputNomeCadastro) {
        this.inputNomeCadastro = inputNomeCadastro;
    }

    public Integer getInputCPFCadastro() {
        return inputCPFCadastro;
    }

    public void setInputCPFCadastro(Integer inputCPFCadastro) {
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

    public Integer getInputTelefone() {
        return inputTelefone;
    }

    public void setInputTelefone(Integer inputTelefone) {
        this.inputTelefone = inputTelefone;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "inputNomeCadastro='" + inputNomeCadastro + '\'' +
                ", inputCPFCadastro=" + inputCPFCadastro +
                ", inputEmailCadastro='" + inputEmailCadastro + '\'' +
                ", inputSenhaCadastro='" + inputSenhaCadastro + '\'' +
                ", inputDataNascimentoCadastro=" + inputDataNascimentoCadastro +
                ", inputTelefone=" + inputTelefone +
                '}';
    }
}
