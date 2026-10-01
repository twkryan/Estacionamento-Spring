package br.gov.sp.etec.estacionamento.service;

import br.gov.sp.etec.estacionamento.entity.Configuracao;
import br.gov.sp.etec.estacionamento.entity.VeiculoEntity;
import br.gov.sp.etec.estacionamento.model.Placa;
import br.gov.sp.etec.estacionamento.model.Veiculo;
import br.gov.sp.etec.estacionamento.repository.ConfiguracaoRepository;
import br.gov.sp.etec.estacionamento.repository.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VeiculoServiceImpl implements VeiculoService {
    private static final int LIMITE_PLACA = 20;
    private static final int LIMITE_MODELO = 120;
    private static final int LIMITE_COR = 80;
    private static final int LIMITE_OBSERVACAO = 255;

    private final VeiculoRepository repository;
    private final ConfiguracaoRepository configuracoes;

    public VeiculoServiceImpl(VeiculoRepository repository, ConfiguracaoRepository configuracoes) {
        this.repository = repository;
        this.configuracoes = configuracoes;
    }

    @Override
    @Transactional
    public void cadastrarVeiculo(Veiculo veiculo) {
        validarCampos(veiculo);
        String placaNormalizada = Placa.normalizar(veiculo.getPlaca());
        Configuracao configuracao = configuracoes.bloquear();
        if (configuracao == null) {
            throw new IllegalStateException("A configuração do estacionamento não foi inicializada.");
        }

        long ocupacao = repository.countByHoraSaidaIsNull();
        if (ocupacao >= configuracao.getCapacidade()) {
            throw new OperacaoInvalidaException("Não há vagas disponíveis para uma nova entrada.");
        }
        boolean placaJaAberta = repository.findByHoraSaidaIsNullOrderByHoraEntradaAsc().stream()
                .anyMatch(entrada -> Placa.normalizar(entrada.getPlaca()).equals(placaNormalizada));
        if (placaJaAberta) {
            throw new OperacaoInvalidaException("Esta placa já possui uma entrada aberta.");
        }
        repository.save(toVeiculoEntity(veiculo));
    }

    @Override
    public List<VeiculoEntity> listarEntradasAbertas(String placa) {
        return filtrarPorPlaca(repository.findByHoraSaidaIsNullOrderByHoraEntradaAsc(), placa);
    }

    @Override
    public List<VeiculoEntity> listarHistoricoSaidas(String placa) {
        return filtrarPorPlaca(repository.findByHoraSaidaIsNotNullOrderByHoraSaidaDesc(), placa);
    }

    @Override
    @Transactional
    public boolean registrarSaida(Long id) {
        return repository.registrarSaida(id, LocalDateTime.now()) == 1;
    }

    @Override
    public List<VeiculoEntity> listarVeiculos() {
        List<VeiculoEntity> listaVeiculos = repository.findAll();
        return listaVeiculos;
    }

    @Override
    public boolean excluirVeiculo(Long id) {
        try {
            repository.deleteById(id);
            return true;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public VeiculoEntity atualizarVeiculo(VeiculoEntity veiculoEntity) {
        return repository.save(veiculoEntity);
    }


    private VeiculoEntity toVeiculoEntity(Veiculo veiculo) {
        VeiculoEntity veiculoEntity = new VeiculoEntity();
        veiculoEntity.setHoraEntrada(LocalDateTime.now());
        veiculoEntity.setCor(veiculo.getCor().trim());
        veiculoEntity.setModelo(veiculo.getModelo().trim());
        veiculoEntity.setObservacao(veiculo.getObservacao() == null ? "" : veiculo.getObservacao().trim());
        veiculoEntity.setPlaca(veiculo.getPlaca().trim());
        return veiculoEntity;
    }

    private void validarCampos(Veiculo veiculo) {
        if (veiculo == null || Placa.normalizar(veiculo.getPlaca()).isEmpty()
                || veiculo.getModelo() == null || veiculo.getModelo().isBlank()
                || veiculo.getCor() == null || veiculo.getCor().isBlank()) {
            throw new OperacaoInvalidaException("Preencha placa, modelo e cor para registrar a entrada.");
        }
        validarLimite("placa", veiculo.getPlaca(), LIMITE_PLACA);
        validarLimite("modelo", veiculo.getModelo(), LIMITE_MODELO);
        validarLimite("cor", veiculo.getCor(), LIMITE_COR);
        validarLimite("observação", veiculo.getObservacao(), LIMITE_OBSERVACAO);
    }

    private void validarLimite(String campo, String valor, int limite) {
        if (valor != null && valor.length() > limite) {
            throw new OperacaoInvalidaException("O campo " + campo + " deve ter no máximo "
                    + limite + " caracteres.");
        }
    }

    private List<VeiculoEntity> filtrarPorPlaca(List<VeiculoEntity> entradas, String placa) {
        String busca = Placa.normalizar(placa);
        if (busca.isEmpty()) return entradas;
        return entradas.stream()
                .filter(entrada -> Placa.normalizar(entrada.getPlaca()).contains(busca))
                .toList();
    }

}
