package br.gov.sp.etec.estacionamento.config;
import br.gov.sp.etec.estacionamento.entity.Configuracao;
import br.gov.sp.etec.estacionamento.repository.ConfiguracaoRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.*;

@Configuration
public class Inicializacao {
    @Bean
    ApplicationRunner prepararConfiguracao(ConfiguracaoRepository repository) {
        return args -> {
            if (!repository.existsById(1L)) repository.saveAndFlush(new Configuracao());
        };
    }
}
