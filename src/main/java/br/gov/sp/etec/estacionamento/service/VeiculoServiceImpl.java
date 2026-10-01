package br.gov.sp.etec.estacionamento.service;

import br.gov.sp.etec.estacionamento.entity.VeiculoEntity;
import br.gov.sp.etec.estacionamento.model.Veiculo;
import br.gov.sp.etec.estacionamento.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VeiculoServiceImpl implements VeiculoService {

    @Autowired
    VeiculoRepository repository;

    @Override
    public void cadastrarVeiculo(Veiculo veiculo) {
        repository.save(toVeiculoEntity(veiculo));
    }

    @Override
    public List<VeiculoEntity> listarEntradasAbertas(String placa) {
        return repository.findByHoraSaidaIsNullAndPlacaContainingIgnoreCaseOrderByHoraEntradaAsc(placa.trim());
    }

    @Override
    public List<VeiculoEntity> listarHistoricoSaidas(String placa) {
        return repository.findByHoraSaidaIsNotNullAndPlacaContainingIgnoreCaseOrderByHoraSaidaDesc(placa.trim());
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
        veiculoEntity.setCor(veiculo.getCor());
        veiculoEntity.setModelo(veiculo.getModelo());
        veiculoEntity.setObservacao(veiculo.getObservacao());
        veiculoEntity.setPlaca(veiculo.getPlaca());
        return veiculoEntity;
    }

}
