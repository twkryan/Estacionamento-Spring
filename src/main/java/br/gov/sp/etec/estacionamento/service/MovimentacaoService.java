package br.gov.sp.etec.estacionamento.service;

import br.gov.sp.etec.estacionamento.entity.VeiculoEntity;
import br.gov.sp.etec.estacionamento.model.FiltroMovimentacoes;
import br.gov.sp.etec.estacionamento.model.MovimentacaoDTO;
import br.gov.sp.etec.estacionamento.model.ResultadoMovimentacoes;
import br.gov.sp.etec.estacionamento.repository.MovimentacaoRepository;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoService {
    private final MovimentacaoRepository movimentacoes;
    private final Clock clock;

    public MovimentacaoService(MovimentacaoRepository movimentacoes, Clock consultaClock) {
        this.movimentacoes = movimentacoes;
        this.clock = consultaClock;
    }

    public ResultadoMovimentacoes consultar(FiltroMovimentacoes filtro) {
        LocalDateTime inicio = filtro.dataInicio() == null ? null : filtro.dataInicio().atStartOfDay();
        LocalDateTime fimExclusivo = filtro.dataFim() == null
                ? null
                : filtro.dataFim().plusDays(1).atStartOfDay();
        String placa = PlacaMovimentacoes.normalizar(filtro.placa());
        LocalDateTime agora = LocalDateTime.now(clock);

        List<MovimentacaoDTO> resultado = movimentacoes.pesquisar(placa, inicio, fimExclusivo).stream()
                .map(veiculo -> converter(veiculo, agora))
                .toList();
        List<Long> permanenciasEncerradas = resultado.stream()
                .filter(movimentacao -> !movimentacao.aberta())
                .map(MovimentacaoDTO::permanenciaMinutos)
                .toList();
        Long media = permanenciasEncerradas.isEmpty()
                ? null
                : Math.round(permanenciasEncerradas.stream().mapToLong(Long::longValue).average().orElseThrow());

        return new ResultadoMovimentacoes(resultado, resultado.size(), media);
    }

    private MovimentacaoDTO converter(VeiculoEntity veiculo, LocalDateTime agora) {
        LocalDateTime fimPermanencia = veiculo.getHoraSaida() == null ? agora : veiculo.getHoraSaida();
        return new MovimentacaoDTO(
                veiculo.getId(),
                veiculo.getPlaca(),
                veiculo.getModelo(),
                veiculo.getHoraEntrada(),
                veiculo.getHoraSaida(),
                veiculo.getHoraSaida() == null,
                MovimentacaoDTO.calcularPermanencia(veiculo.getHoraEntrada(), fimPermanencia)
        );
    }
}
