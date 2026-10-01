package br.gov.sp.etec.estacionamento.service;

import br.gov.sp.etec.estacionamento.model.DadosRelatorio;
import br.gov.sp.etec.estacionamento.model.FiltroMovimentacoes;
import org.springframework.stereotype.Service;

@Service
public class RelatorioService {
    private final MovimentacaoService movimentacoes;
    private final ConfiguracaoService configuracoes;

    public RelatorioService(MovimentacaoService movimentacoes, ConfiguracaoService configuracoes) {
        this.movimentacoes = movimentacoes;
        this.configuracoes = configuracoes;
    }

    public DadosRelatorio consultar(FiltroMovimentacoes filtro) {
        return new DadosRelatorio(movimentacoes.consultar(filtro), configuracoes.indicadores());
    }
}
