package br.gov.sp.etec.estacionamento.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Configuracao {
    @Id private Long id = 1L;
    private int capacidade = 30;
    private boolean notificacoes = true;
    private boolean backup = true;
    private boolean exportacao = true;
    public Long getId() { return id; }
    public int getCapacidade() { return capacidade; }
    public void setCapacidade(int capacidade) { this.capacidade = capacidade; }
    public boolean isNotificacoes() { return notificacoes; }
    public void setNotificacoes(boolean notificacoes) { this.notificacoes = notificacoes; }
    public boolean isBackup() { return backup; }
    public void setBackup(boolean backup) { this.backup = backup; }
    public boolean isExportacao() { return exportacao; }
    public void setExportacao(boolean exportacao) { this.exportacao = exportacao; }
}
