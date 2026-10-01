package br.gov.sp.etec.estacionamento;

import br.gov.sp.etec.estacionamento.entity.VeiculoEntity;
import br.gov.sp.etec.estacionamento.repository.ConfiguracaoRepository;
import br.gov.sp.etec.estacionamento.repository.VeiculoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RelatorioHttpTests extends HttpTestSupport {
    @Autowired
    private ConfiguracaoRepository configuracoes;

    @Autowired
    private VeiculoRepository veiculos;

    @Test
    void relatorioAplicaFiltrosSemFiltrarOcupacaoAtual() throws Exception {
        iniciarAdmin();
        salvar("ABC-1234", "Visita encerrada", LocalDateTime.of(2025, 7, 1, 8, 30),
                LocalDateTime.of(2025, 7, 1, 10, 0));
        salvar("ZZZ-9999", "Visita aberta fora do período", LocalDateTime.of(2025, 7, 3, 10, 0), null);

        var pagina = pagina("/relatorios?placa=ABC1234&dataInicio=2025-07-01&dataFim=2025-07-01", admin);
        var painel = pagina("/painel", admin);

        assertThat(pagina.selectFirst("h1").text()).isEqualTo("Relatórios");
        assertThat(pagina.selectFirst("#total-movimentacoes").text()).isEqualTo("1");
        assertThat(pagina.selectFirst("#media-permanencia").text()).isEqualTo("1h 30min");
        assertThat(pagina.selectFirst("#capacidade-atual").text()).isEqualTo("30");
        assertThat(pagina.selectFirst("#ocupacao-atual").text()).isEqualTo("1");
        assertThat(pagina.selectFirst("#vagas-disponiveis").text()).isEqualTo("29");
        assertThat(pagina.selectFirst("#percentual-ocupacao").text()).isEqualTo("3.3%");
        assertThat(pagina.selectFirst("#capacidade-atual").text())
                .isEqualTo(painel.selectFirst("#capacidade-atual").text());
        assertThat(pagina.selectFirst("#ocupacao-atual").text())
                .isEqualTo(painel.selectFirst("#ocupacao-atual").text());
        assertThat(pagina.selectFirst("#vagas-disponiveis").text())
                .isEqualTo(painel.selectFirst("#vagas-disponiveis").text());
        assertThat(pagina.selectFirst("#exportar-csv").attr("href"))
                .contains("placa=ABC1234", "dataInicio=2025-07-01", "dataFim=2025-07-01");
        assertThat(pagina.select("nav a[href='/relatorios']")).hasSize(1);
        assertThat(pagina.select(".tabela[role=region][aria-label='Movimentações do relatório'][tabindex=0]")).hasSize(1);
        assertThat(pagina.selectFirst("#orientacao-rolagem").text())
                .contains("Em telas estreitas, deslize a tabela");
        assertThat(pagina.select("#tabela-movimentacoes tbody tr")).hasSize(1);
        assertThat(pagina.text()).doesNotContain("Visita aberta fora do período");
    }

    @Test
    void csvFiltraVisitasIncluiResumoEProtegeFormulaDePlanilha() throws Exception {
        iniciarAdmin();
        salvar("ABC-1234", "  \uFEFF=SOMA(1;1) Veículo elétrico", LocalDateTime.of(2025, 7, 1, 8, 30),
                LocalDateTime.of(2025, 7, 1, 10, 0));
        salvar("ABC-5678", "Visita aberta no período", LocalDateTime.of(2025, 7, 1, 12, 0), null);
        salvar("FORA-9999", "Não incluir", LocalDateTime.of(2025, 7, 2, 10, 0),
                LocalDateTime.of(2025, 7, 2, 11, 0));
        salvar("ABC-0000", "Aberto fora do período", LocalDateTime.of(2025, 7, 3, 10, 0), null);

        var resposta = mvc.perform(get("/relatorios/exportar")
                        .param("placa", "ABC")
                        .param("dataInicio", "2025-07-01")
                        .param("dataFim", "2025-07-01")
                        .session(admin))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        String csv = new String(resposta.getContentAsByteArray(), StandardCharsets.UTF_8);

        assertThat(resposta.getContentType()).isEqualTo("text/csv;charset=UTF-8");
        assertThat(resposta.getHeader("Content-Disposition"))
                .isEqualTo("attachment; filename=\"relatorio-movimentacoes.csv\"");
        assertThat(csv).startsWith("\uFEFF");
        assertThat(csv).contains("\"'  \uFEFF=SOMA(1;1) Veículo elétrico\"", "Visita aberta no período", "\"Aberta\"",
                "\"—\"", "Total de movimentações", "\"2\"", "1h 30min", "Ocupação atual", "\"30\"",
                "\"2\"", "\"28\"", "6.7%");
        assertThat(csv).doesNotContain("FORA-9999", "Não incluir", "Aberto fora do período");
    }

    @Test
    void operadorPodeConsultarERelatarQuandoExportacaoEstaHabilitada() throws Exception {
        iniciarAdmin();
        mvc.perform(cadastro("operador@example.com", "OPERADOR").session(admin))
                .andExpect(status().is3xxRedirection());
        var operador = login("operador@example.com", "SenhaDeTeste");

        var pagina = pagina("/relatorios", operador);
        var resposta = mvc.perform(get("/relatorios/exportar").session(operador))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        assertThat(pagina.selectFirst("h1").text()).isEqualTo("Relatórios");
        assertThat(pagina.select("nav a[href='/relatorios']")).hasSize(1);
        assertThat(pagina.selectFirst("#exportar-csv")).isNotNull();
        assertThat(resposta.getContentType()).isEqualTo("text/csv;charset=UTF-8");
    }

    @Test
    void exportacaoDesabilitadaOcultaOpcaoEImpedeDownloadParaAdminEOperador() throws Exception {
        iniciarAdmin();
        mvc.perform(cadastro("operador@example.com", "OPERADOR").session(admin))
                .andExpect(status().is3xxRedirection());
        var operador = login("operador@example.com", "SenhaDeTeste");
        configuracoes.findById(1L).orElseThrow().setExportacao(false);

        for (var sessao : new org.springframework.mock.web.MockHttpSession[]{admin, operador}) {
            var pagina = pagina("/relatorios", sessao);
            var resposta = mvc.perform(get("/relatorios/exportar").session(sessao))
                    .andExpect(status().isForbidden())
                    .andReturn().getResponse();

            assertThat(pagina.select("#exportar-csv")).isEmpty();
            assertThat(pagina.selectFirst("#exportacao-desabilitada")).isNotNull();
            assertThat(resposta.getContentAsByteArray()).isEmpty();
            assertThat(resposta.getHeader("Content-Disposition")).isNull();
        }
    }

    @Test
    void relatorioSemResultadosEPeriodoInvalidoMostramEstadosClaros() throws Exception {
        iniciarAdmin();

        var vazio = pagina("/relatorios", admin);
        var invalido = pagina("/relatorios?dataInicio=2025-07-02&dataFim=2025-07-01", admin);
        mvc.perform(get("/relatorios/exportar")
                        .param("dataInicio", "2025-07-02")
                        .param("dataFim", "2025-07-01")
                        .session(admin))
                .andExpect(status().isBadRequest());

        assertThat(vazio.selectFirst("#estado-vazio").text()).isEqualTo("Nenhuma movimentação encontrada.");
        assertThat(vazio.select("#media-permanencia")).isEmpty();
        assertThat(vazio.selectFirst("#sem-media")).isNotNull();
        assertThat(invalido.selectFirst(".mensagem.erro").text())
                .isEqualTo("A data inicial não pode ser posterior à data final.");
        assertThat(invalido.selectFirst("#total-movimentacoes").text()).isEqualTo("0");
    }

    @Test
    void relatorioECsvExigemAutenticacao() throws Exception {
        mvc.perform(get("/relatorios")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/relatorios/exportar")).andExpect(status().is3xxRedirection());
    }

    private VeiculoEntity salvar(String placa, String modelo, LocalDateTime entrada, LocalDateTime saida) {
        VeiculoEntity veiculo = new VeiculoEntity();
        veiculo.setPlaca(placa);
        veiculo.setModelo(modelo);
        veiculo.setHoraEntrada(entrada);
        veiculo.setHoraSaida(saida);
        return veiculos.save(veiculo);
    }
}
