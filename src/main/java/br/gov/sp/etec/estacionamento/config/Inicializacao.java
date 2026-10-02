package br.gov.sp.etec.estacionamento.config;
import br.gov.sp.etec.estacionamento.entity.Configuracao;
import br.gov.sp.etec.estacionamento.repository.ConfiguracaoRepository;
import br.gov.sp.etec.estacionamento.repository.VeiculoRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;

@Configuration
public class Inicializacao {
    @Bean
    ApplicationRunner prepararConfiguracao(ConfiguracaoRepository repository, VeiculoRepository veiculos) {
        return args -> {
            if (!repository.existsById(1L)) {
                Configuracao configuracao = new Configuracao();
                configuracao.setCapacidade(Math.toIntExact(Math.max(30L, veiculos.countByHoraSaidaIsNull())));
                repository.saveAndFlush(configuracao);
            }
        };
    }
}
