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
    @jakarta.persistence.Column(name = "input_cpf_cadastro")
    private String cpfLegado;
    private String inputEmailCadastro;
    private String inputSenhaCadastro;
    private LocalDate inputDataNascimentoCadastro;
    private String inputTelefone;
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private Papel papel;
    private Boolean ativo;

    public Papel getPapel() { return papel; }
    public void setPapel(Papel papel) { this.papel = papel; }
    public boolean isAtivo() { return Boolean.TRUE.equals(ativo); }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

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
        return inputCPFCadastro != null && !inputCPFCadastro.isBlank() ? inputCPFCadastro : cpfLegado;
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
