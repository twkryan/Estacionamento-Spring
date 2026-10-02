package br.gov.sp.etec.estacionamento.service;

import br.gov.sp.etec.estacionamento.entity.Configuracao;
import br.gov.sp.etec.estacionamento.model.ConfiguracaoAtual;
import br.gov.sp.etec.estacionamento.model.IndicadoresVagas;
import br.gov.sp.etec.estacionamento.repository.ConfiguracaoRepository;
import br.gov.sp.etec.estacionamento.repository.VeiculoRepository;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

@Service
public class ConfiguracaoService {
    private final ConfiguracaoRepository configuracoes;
    private final VeiculoRepository veiculos;
    private final JdbcTemplate jdbc;

    public ConfiguracaoService(ConfiguracaoRepository configuracoes, VeiculoRepository veiculos, JdbcTemplate jdbc) {
        this.configuracoes = configuracoes;
        this.veiculos = veiculos;
        this.jdbc = jdbc;
    }

    @Transactional(readOnly = true)
    public ConfiguracaoAtual obter() {
        return snapshot(configuracoes.findById(1L).orElseThrow());
    }

    @Transactional(readOnly = true)
    public IndicadoresVagas indicadores() {
        var configuracao = configuracoes.findById(1L).orElseThrow();
        long ocupacao = veiculos.countByHoraSaidaIsNull();
        int capacidade = configuracao.getCapacidade();
        long vagasDisponiveis = capacidade - ocupacao;
        double percentual = capacidade == 0 ? 0 : ocupacao * 100.0 / capacidade;
        return new IndicadoresVagas(capacidade, ocupacao, vagasDisponiveis, percentual);
    }

    @Transactional
    public void salvarCapacidade(int capacidade) {
        if (capacidade <= 0) {
            throw new OperacaoInvalidaException("A capacidade deve ser maior que zero.");
        }
        Configuracao configuracao = configuracoes.bloquear();
        if (configuracao == null) {
            throw new IllegalStateException("A configuração do estacionamento não foi inicializada.");
        }
        long ocupacao = veiculos.countByHoraSaidaIsNull();
        if (capacidade < ocupacao) {
            throw new OperacaoInvalidaException("A capacidade não pode ser menor que a ocupação atual.");
        }
        configuracao.setCapacidade(capacidade);
    }

    @Transactional
    public void salvarOpcoes(boolean notificacoes, boolean backup, boolean exportacao) {
        Configuracao configuracao = configuracoes.bloquear();
        if (configuracao == null) {
            throw new IllegalStateException("A configuração do estacionamento não foi inicializada.");
        }
        configuracao.setNotificacoes(notificacoes);
        configuracao.setBackup(backup);
        configuracao.setExportacao(exportacao);
    }

    public String gerarBackupSql() {
        return jdbc.execute((ConnectionCallback<String>) connection -> {
            int isolamentoOriginal = connection.getTransactionIsolation();
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                try (var statement = connection.createStatement();
                     var resultado = statement.executeQuery("SCRIPT SIMPLE COLUMNS NOPASSWORDS")) {
                    List<String> comandos = new ArrayList<>();
                    while (resultado.next()) {
                        comandos.add(resultado.getString(1));
                    }
                    return String.join(System.lineSeparator(), comandos) + System.lineSeparator();
                }
            } finally {
                connection.setTransactionIsolation(isolamentoOriginal);
            }
        });
    }

    private ConfiguracaoAtual snapshot(Configuracao configuracao) {
        return new ConfiguracaoAtual(configuracao.getCapacidade(), configuracao.isNotificacoes(),
                configuracao.isBackup(), configuracao.isExportacao());
    }
}
