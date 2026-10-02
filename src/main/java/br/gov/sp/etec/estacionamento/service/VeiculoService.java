package br.gov.sp.etec.estacionamento.service;

import br.gov.sp.etec.estacionamento.entity.VeiculoEntity;
import br.gov.sp.etec.estacionamento.model.Veiculo;

import java.util.List;

public interface VeiculoService {
    // cadastrar
    // deletar
    // atualizar
    // listar
    void cadastrarVeiculo(Veiculo veiculo);
    List<VeiculoEntity> listarEntradasAbertas(String placa);
    List<VeiculoEntity> listarHistoricoSaidas(String placa);
    boolean registrarSaida(Long id);
    List<VeiculoEntity> listarVeiculos();
    boolean excluirVeiculo(Long id);
    VeiculoEntity atualizarVeiculo(VeiculoEntity veiculoEntity);
}
