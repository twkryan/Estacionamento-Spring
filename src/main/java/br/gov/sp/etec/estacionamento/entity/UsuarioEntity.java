package br.gov.sp.etec.estacionamento.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity(name = "tb_usuario")
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String inputNomeCadastro;
    private String inputCPFCadastro;
    private String inputEmailCadastro;
    private String inputSenhaCadastro;
    private LocalDate inputDataNascimentoCadastro;
    private String inputTelefone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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


}
