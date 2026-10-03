package br.gov.sp.etec.estacionamento;

import br.gov.sp.etec.estacionamento.entity.VeiculoEntity;
import br.gov.sp.etec.estacionamento.repository.VeiculoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Import(MovimentacaoHttpTests.RelogioFixo.class)
class MovimentacaoHttpTests extends HttpTestSupport {
    @Autowired
    private VeiculoRepository veiculos;

    @Test
    void usuarioAutenticadoConsultaMovimentacoesSemHistorico() throws Exception {
        iniciarAdmin();

        var pagina = pagina("/movimentacoes", admin);

        assertThat(pagina.selectFirst("h1").text()).isEqualTo("Movimentações");
        assertThat(pagina.select("nav a[href='/movimentacoes']")).hasSize(1);
        assertThat(pagina.selectFirst("#total-movimentacoes").text()).isEqualTo("0");
        assertThat(pagina.text()).contains("Nenhuma movimentação encontrada");
        assertThat(pagina.select("#media-permanencia")).isEmpty();
        assertThat(pagina.select("nav a[aria-current=page]").attr("href")).isEqualTo("/movimentacoes");
        assertThat(pagina.select("main#conteudo-principal[tabindex=-1]")).hasSize(1);
    }

    @Test
    void movimentacoesExigemLogin() throws Exception {
        mvc.perform(get("/movimentacoes")).andExpect(status().is3xxRedirection());
    }

    @Test
    void buscaPorPlacaIgnoraCaixaEspacosEHifenMasPreservaPlacaExibida() throws Exception {
        iniciarAdmin();
        salvar(" ab-12 3cd ", "Sedan", LocalDateTime.of(2025, 7, 1, 10, 15), null);
        salvar("AB\u00a0-12 3CD", "Placa com espaço Unicode", LocalDateTime.of(2025, 7, 1, 10, 20), null);
        salvar("ZZZ-9999", "Hatch", LocalDateTime.of(2025, 7, 1, 11, 0), null);

        var pagina = pagina("/movimentacoes?placa=AB123", admin);

        assertThat(pagina.select("#tabela-movimentacoes tbody tr")).hasSize(2);
        assertThat(pagina.select(".tabela[role=region][aria-label='Tabela de movimentações'][tabindex=0]")).hasSize(1);
        assertThat(pagina.selectFirst("#orientacao-rolagem").text())
                .contains("Em telas estreitas, deslize a tabela");
        assertThat(pagina.select("#tabela-movimentacoes tbody tr").text())
                .contains("ab-12 3cd", "Sedan", "Placa com espaço Unicode");
        assertThat(pagina.selectFirst("#total-movimentacoes").text()).isEqualTo("2");
    }

    @Test
    void periodoDeEntradaInclusivoPodeSerCombinadoComPlaca() throws Exception {
        iniciarAdmin();
        salvar("ABC-1234", "Início do período", LocalDateTime.of(2025, 7, 1, 0, 0), null);
        salvar("A BC 1234", "Fim do período", LocalDateTime.of(2025, 7, 1, 23, 59, 59), null);
        salvar("ABC-1234", "Fora do período", LocalDateTime.of(2025, 7, 2, 0, 0), null);
        salvar("ZZZ-9999", "Outra placa", LocalDateTime.of(2025, 7, 1, 12, 0), null);

        var pagina = pagina("/movimentacoes?placa=abc1234&dataInicio=2025-07-01&dataFim=2025-07-01", admin);

        assertThat(pagina.select("#tabela-movimentacoes tbody tr")).hasSize(2);
        assertThat(pagina.select("#tabela-movimentacoes tbody tr").text())
                .contains("Início do período", "Fim do período")
                .doesNotContain("Fora do período", "Outra placa");
        assertThat(pagina.selectFirst("form[aria-label='Filtros de movimentações']").attr("method")).isEqualTo("get");
        assertThat(pagina.selectFirst("#placa").val()).isEqualTo("abc1234");
        assertThat(pagina.selectFirst("#dataInicio").val()).isEqualTo("2025-07-01");
        assertThat(pagina.selectFirst("#dataFim").val()).isEqualTo("2025-07-01");
        assertThat(pagina.select("a").stream().filter(a -> a.text().equals("Limpar filtros"))
                .map(a -> a.attr("href"))).containsExactly("/movimentacoes");
        assertThat(pagina("/movimentacoes", admin).selectFirst("#total-movimentacoes").text()).isEqualTo("4");
    }

    @Test
    void periodoInvertidoApresentaErroEPreservaFiltros() throws Exception {
        iniciarAdmin();

        var pagina = pagina("/movimentacoes?placa=ABC&dataInicio=2025-07-02&dataFim=2025-07-01", admin);

        assertThat(pagina.selectFirst(".mensagem.erro").text())
                .isEqualTo("A data inicial não pode ser posterior à data final.");
        assertThat(pagina.selectFirst("#placa").val()).isEqualTo("ABC");
        assertThat(pagina.selectFirst("#dataInicio").val()).isEqualTo("2025-07-02");
        assertThat(pagina.selectFirst("#dataFim").val()).isEqualTo("2025-07-01");
        assertThat(pagina.select("#total-movimentacoes, #media-permanencia, #sem-media, #estado-vazio, #tabela-movimentacoes")).isEmpty();
        assertThat(pagina.selectFirst("#estado-invalido").text()).contains("Consulta não realizada", "Corrija os filtros");
    }

    @Test
    void datasInvalidasApresentamErroNaPropriaPagina() throws Exception {
        iniciarAdmin();

        var pagina = pagina("/movimentacoes?dataInicio=2025-02-30", admin);

        assertThat(pagina.selectFirst(".mensagem.erro").text()).isEqualTo("Informe datas de entrada válidas.");
        assertThat(pagina.selectFirst("#dataInicio").val()).isEqualTo("2025-02-30");
        assertThat(pagina.select("#total-movimentacoes, #estado-vazio, #tabela-movimentacoes")).isEmpty();
        assertThat(pagina.selectFirst("#estado-invalido")).isNotNull();
    }

    @Test
    void operadorAutenticadoTambémConsultaMovimentacoes() throws Exception {
        iniciarAdmin();
        mvc.perform(cadastro("operador@example.com", "OPERADOR").session(admin))
                .andExpect(status().is3xxRedirection());
        var operador = login("operador@example.com", "SenhaDeTeste");

        var pagina = pagina("/movimentacoes", operador);

        assertThat(pagina.selectFirst("h1").text()).isEqualTo("Movimentações");
        assertThat(pagina.select("nav a[href='/movimentacoes']")).hasSize(1);
        assertThat(pagina.select("nav a[aria-current=page]").attr("href")).isEqualTo("/movimentacoes");
    }

    @Test
    void consultaComApenasEntradasAbertasNaoMostraMedia() throws Exception {
        iniciarAdmin();
        salvar("ABC-1234", "Veículo aberto", LocalDateTime.of(2025, 7, 3, 8, 17), null);

        var pagina = pagina("/movimentacoes", admin);

        assertThat(pagina.selectFirst("#total-movimentacoes").text()).isEqualTo("1");
        assertThat(pagina.selectFirst("#sem-media").text())
                .contains("Não há visitas encerradas para calcular a média");
        assertThat(pagina.select("#media-permanencia")).isEmpty();
    }

    @Test
    void permanenciaAtravessaMeiaNoiteEVisitaAbertaVaiAteOMomentoDaConsulta() throws Exception {
        iniciarAdmin();
        salvar("ABC-1234", "Cruza meia-noite", LocalDateTime.of(2025, 7, 1, 23, 30),
                LocalDateTime.of(2025, 7, 2, 1, 5));
        salvar("DEF-5678", "Outra encerrada", LocalDateTime.of(2025, 7, 2, 9, 0),
                LocalDateTime.of(2025, 7, 2, 10, 0));
        salvar("GHI-9012", "Ainda aberta", LocalDateTime.of(2025, 7, 3, 8, 17), null);

        var pagina = pagina("/movimentacoes", admin);
        var linhas = pagina.select("#tabela-movimentacoes tbody tr");

        assertThat(linhas).hasSize(3);
        assertThat(linhas.text()).contains("1h 35min", "0h 43min", "Visita aberta");
        assertThat(pagina.selectFirst("#total-movimentacoes").text()).isEqualTo("3");
        assertThat(pagina.selectFirst("#media-permanencia").text()).isEqualTo("1h 18min");
    }

    private VeiculoEntity salvar(String placa, String modelo, LocalDateTime entrada, LocalDateTime saida) {
        VeiculoEntity veiculo = new VeiculoEntity();
        veiculo.setPlaca(placa);
        veiculo.setModelo(modelo);
        veiculo.setHoraEntrada(entrada);
        veiculo.setHoraSaida(saida);
        return veiculos.save(veiculo);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class RelogioFixo {
        @Bean
        @Primary
        Clock relogioFixoMovimentacoes() {
            return Clock.fixed(Instant.parse("2025-07-03T12:00:00Z"), ZoneId.of("America/Sao_Paulo"));
        }
    }
}
