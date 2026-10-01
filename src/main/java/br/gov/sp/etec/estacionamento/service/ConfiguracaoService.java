package br.gov.sp.etec.estacionamento.service;

import br.gov.sp.etec.estacionamento.entity.Configuracao;
import br.gov.sp.etec.estacionamento.model.ConfiguracaoAtual;
import br.gov.sp.etec.estacionamento.model.IndicadoresVagas;
import br.gov.sp.etec.estacionamento.repository.ConfiguracaoRepository;
import br.gov.sp.etec.estacionamento.repository.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfiguracaoService {
    private final ConfiguracaoRepository configuracoes;
    private final VeiculoRepository veiculos;

    public ConfiguracaoService(ConfiguracaoRepository configuracoes, VeiculoRepository veiculos) {
        this.configuracoes = configuracoes;
        this.veiculos = veiculos;
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

    private ConfiguracaoAtual snapshot(Configuracao configuracao) {
        return new ConfiguracaoAtual(configuracao.getCapacidade(), configuracao.isNotificacoes(),
                configuracao.isBackup(), configuracao.isExportacao());
    }
}
