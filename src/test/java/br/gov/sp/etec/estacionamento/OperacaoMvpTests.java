package br.gov.sp.etec.estacionamento;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OperacaoMvpTests extends HttpTestSupport {
    @BeforeEach
    void autenticarAdmin() throws Exception { iniciarAdmin(); }

    @Test
    void formularioDeEntradaMantemPostCsrfEStatusNeutroAntesDoEnvio() throws Exception {
        var page = pagina("/veiculo/registrar-entrada", admin);
        var form = page.selectFirst("form[data-feedback-envio]");

        assertThat(form).isNotNull();
        assertThat(form.attr("method")).isEqualTo("post");
        assertThat(form.attr("action")).isEqualTo("/veiculo/cadastrar");
        assertThat(form.selectFirst("input[name=_csrf]")).isNotNull();
        var status = form.selectFirst("[data-feedback-envio-status][role=status][hidden]");
        assertThat(status).isNotNull();
        assertThat(status.text()).isEmpty();
        assertThat(form.selectFirst("button[type=submit]").hasAttr("disabled")).isFalse();
        assertThat(page.select("script[src='/js/feedback-envio.js']")).hasSize(1);
    }

    @Test
    void painelMostraCapacidadeOcupacaoVagasEEstadoVazio() throws Exception {
        var page = pagina("/painel", admin);

        assertThat(page.selectFirst("#capacidade-atual").text()).isEqualTo("30");
        assertThat(page.selectFirst("#ocupacao-atual").text()).isEqualTo("0");
        assertThat(page.selectFirst("#vagas-disponiveis").text()).isEqualTo("30");
        assertThat(page.select("#painel-entradas thead th").eachText())
                .contains("Placa", "Modelo", "Entrada", "Status");
        assertThat(page.select("#painel-entradas tbody tr")).isEmpty();
        assertThat(page.selectFirst("#estado-vazio").text())
                .contains("Nenhuma entrada aberta");
    }

    @Test
    void entradaEsaidaAtualizamListaEOcupacaoNoPainel() throws Exception {
        mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                        .param("placa", " painel7-89 ").param("modelo", "Modelo do painel")
                        .param("cor", "Azul").param("observacao", "Teste"))
                .andExpect(status().isOk());

        var painelComEntrada = pagina("/painel", admin);
        assertThat(painelComEntrada.selectFirst("#capacidade-atual").text()).isEqualTo("30");
        assertThat(painelComEntrada.selectFirst("#ocupacao-atual").text()).isEqualTo("1");
        assertThat(painelComEntrada.selectFirst("#vagas-disponiveis").text()).isEqualTo("29");
        assertThat(painelComEntrada.select("#painel-entradas tbody tr")).hasSize(1);
        assertThat(painelComEntrada.select("#painel-entradas tbody tr").first().text())
                .contains("painel7-89", "Modelo do painel", "Entrada aberta");

        var saida = mvc.perform(get("/veiculo/registrar-saida").session(admin).param("placa", "painel7-89"))
                .andExpect(status().isOk()).andReturn();
        String id = org.jsoup.Jsoup.parse(saida.getResponse().getContentAsString())
                .selectFirst("#entradas-abertas input[name=id]").val();
        mvc.perform(post("/veiculo/registrar-saida").session(admin).with(csrf()).param("id", id))
                .andExpect(status().is3xxRedirection());

        var painelVazio = pagina("/painel", admin);
        assertThat(painelVazio.selectFirst("#ocupacao-atual").text()).isEqualTo("0");
        assertThat(painelVazio.selectFirst("#vagas-disponiveis").text()).isEqualTo("30");
        assertThat(painelVazio.select("#painel-entradas tbody tr")).isEmpty();
        assertThat(painelVazio.selectFirst("#estado-vazio").text()).contains("Nenhuma entrada aberta");
    }

    @Test
    void placaDuplicadaComEspacosEHifenEhRejeitadaEFormularioPreservaDados() throws Exception {
        mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                        .param("placa", " ab-123 ").param("modelo", "Modelo original").param("cor", "Prata"))
                .andExpect(status().isOk());

        var duplicada = mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                        .param("placa", " A B 1 2 3 ").param("modelo", "Modelo preenchido").param("cor", "Azul"))
                .andExpect(status().isOk()).andReturn();
        var formulario = org.jsoup.Jsoup.parse(duplicada.getResponse().getContentAsString());

        assertThat(formulario.selectFirst(".erro").text()).contains("já possui uma entrada aberta");
        assertThat(formulario.selectFirst("#placa").val()).isEqualTo(" A B 1 2 3 ");
        assertThat(formulario.selectFirst("#modelo").val()).isEqualTo("Modelo preenchido");
        assertThat(pagina("/painel", admin).select("#painel-entradas tbody tr")).hasSize(1);
    }

    @Test
    void camposObrigatoriosSaoValidadosNoServidor() throws Exception {
        var response = mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                        .param("placa", "  ").param("modelo", "").param("cor", ""))
                .andExpect(status().isOk()).andReturn();
        var formulario = org.jsoup.Jsoup.parse(response.getResponse().getContentAsString());

        assertThat(formulario.selectFirst(".erro").text()).contains("Preencha placa, modelo e cor");
        assertThat(formulario.selectFirst("#placa").val()).isEqualTo("  ");
        var status = formulario.selectFirst("form[data-feedback-envio] [data-feedback-envio-status][role=status][hidden]");
        assertThat(status).isNotNull();
        assertThat(status.text()).isEmpty();
        assertThat(pagina("/painel", admin).select("#painel-entradas tbody tr")).isEmpty();
    }

    @Test
    void limitesDosCamposSaoValidadosNoServidorEValoresSaoPreservados() throws Exception {
        var formularioInicial = pagina("/veiculo/registrar-entrada", admin);
        assertThat(formularioInicial.selectFirst("#placa").attr("maxlength")).isEqualTo("20");
        assertThat(formularioInicial.selectFirst("#modelo").attr("maxlength")).isEqualTo("120");
        assertThat(formularioInicial.selectFirst("#cor").attr("maxlength")).isEqualTo("80");
        assertThat(formularioInicial.selectFirst("#observacao").attr("maxlength")).isEqualTo("255");

        var valoresAcimaDoLimite = java.util.List.of(
                java.util.Map.entry("placa", "P".repeat(21)),
                java.util.Map.entry("modelo", "M".repeat(121)),
                java.util.Map.entry("cor", "C".repeat(81)),
                java.util.Map.entry("observacao", "O".repeat(256)));

        for (var invalido : valoresAcimaDoLimite) {
            String placa = invalido.getKey().equals("placa") ? invalido.getValue() : "LIM1A23";
            String modelo = invalido.getKey().equals("modelo") ? invalido.getValue() : "Modelo válido";
            String cor = invalido.getKey().equals("cor") ? invalido.getValue() : "Prata";
            String observacao = invalido.getKey().equals("observacao") ? invalido.getValue() : "";
            var response = mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                            .param("placa", placa).param("modelo", modelo).param("cor", cor)
                            .param("observacao", observacao))
                    .andExpect(status().isOk()).andReturn();
            var formulario = org.jsoup.Jsoup.parse(response.getResponse().getContentAsString());

            assertThat(formulario.selectFirst(".erro")).isNotNull();
            assertThat(formulario.selectFirst("#" + invalido.getKey()).val()).isEqualTo(invalido.getValue());
        }
        assertThat(pagina("/painel", admin).select("#painel-entradas tbody tr")).isEmpty();
    }

    @Test
    void entradaCheiaEhRejeitadaEIndicadoresPermanecemNoLimite() throws Exception {
        mvc.perform(post("/configuracoes").session(admin).with(csrf()).param("capacidade", "1"))
                .andExpect(status().is3xxRedirection());
        registrarEntrada("LOT1A23");

        var cheia = mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                        .param("placa", "LOT2B34").param("modelo", "Outro veículo").param("cor", "Verde"))
                .andExpect(status().isOk()).andReturn();
        var formulario = org.jsoup.Jsoup.parse(cheia.getResponse().getContentAsString());

        assertThat(formulario.selectFirst(".erro").text()).contains("Não há vagas disponíveis");
        var painel = pagina("/painel", admin);
        assertThat(painel.selectFirst("#capacidade-atual").text()).isEqualTo("1");
        assertThat(painel.selectFirst("#ocupacao-atual").text()).isEqualTo("1");
        assertThat(painel.selectFirst("#vagas-disponiveis").text()).isEqualTo("0");
        assertThat(painel.select("#painel-entradas tbody tr")).hasSize(1);
    }

    @Test
    void novaEntradaDaMesmaPlacaEhPermitidaDepoisDaSaidaNormalizada() throws Exception {
        registrarEntrada("ret-4a56");
        var busca = mvc.perform(get("/veiculo/registrar-saida").session(admin).param("placa", "RET 4A56"))
                .andExpect(status().isOk()).andReturn();
        String id = org.jsoup.Jsoup.parse(busca.getResponse().getContentAsString())
                .selectFirst("#entradas-abertas input[name=id]").val();
        mvc.perform(post("/veiculo/registrar-saida").session(admin).with(csrf()).param("id", id))
                .andExpect(status().is3xxRedirection());

        registrarEntrada(" RET 4-A56 ");

        var painel = pagina("/painel", admin);
        assertThat(painel.selectFirst("#ocupacao-atual").text()).isEqualTo("1");
        assertThat(painel.select("#painel-entradas tbody tr")).hasSize(1);
        assertThat(painel.select("#painel-entradas tbody tr").first().text()).contains("RET 4-A56");
        var historico = mvc.perform(get("/veiculo/registrar-saida").session(admin).param("placa", "ret4a56"))
                .andExpect(status().isOk()).andReturn();
        assertThat(org.jsoup.Jsoup.parse(historico.getResponse().getContentAsString())
                .select("#historico-saidas tr")).hasSize(1);
    }

    @Test
    void capacidadePodeSerAlteradaEPermaneceSalva() throws Exception {
        var inicial = pagina("/configuracoes", admin);
        assertThat(inicial.selectFirst("#capacidade").val()).isEqualTo("30");

        mvc.perform(post("/configuracoes").session(admin).with(csrf()).param("capacidade", "42"))
                .andExpect(status().is3xxRedirection());

        assertThat(pagina("/configuracoes", admin).selectFirst("#capacidade").val()).isEqualTo("42");
        assertThat(pagina("/painel", admin).selectFirst("#capacidade-atual").text()).isEqualTo("42");
    }

    @Test
    void opcoesPersistemEControlamApenasAsConfirmacoesDeSucesso() throws Exception {
        var inicial = pagina("/configuracoes", admin);
        assertThat(inicial.selectFirst("#notificacoes").hasAttr("checked")).isTrue();
        assertThat(inicial.selectFirst("#backup").hasAttr("checked")).isTrue();
        assertThat(inicial.selectFirst("#exportacao").hasAttr("checked")).isTrue();

        var sucessoHabilitado = mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                        .param("placa", "NOT1A23").param("modelo", "Com notificação").param("cor", "Azul"))
                .andExpect(status().isOk()).andReturn();
        assertThat(org.jsoup.Jsoup.parse(sucessoHabilitado.getResponse().getContentAsString())
                .selectFirst(".mensagem").text()).contains("Entrada registrada");

        mvc.perform(post("/configuracoes/opcoes").session(admin).with(csrf())
                        .param("backup", "true"))
                .andExpect(status().is3xxRedirection());

        var opcoesSalvas = pagina("/configuracoes", admin);
        assertThat(opcoesSalvas.selectFirst("#notificacoes").hasAttr("checked")).isFalse();
        assertThat(opcoesSalvas.selectFirst("#backup").hasAttr("checked")).isTrue();
        assertThat(opcoesSalvas.selectFirst("#exportacao").hasAttr("checked")).isFalse();
        assertThat(opcoesSalvas.select(".mensagem")).isEmpty();

        var sucessoDesabilitado = mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                        .param("placa", "NOT2B34").param("modelo", "Sem confirmação").param("cor", "Prata"))
                .andExpect(status().isOk()).andReturn();
        assertThat(org.jsoup.Jsoup.parse(sucessoDesabilitado.getResponse().getContentAsString())
                .select(".mensagem")).isEmpty();

        var erro = mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                        .param("placa", "NOT2B34").param("modelo", "Duplicada").param("cor", "Prata"))
                .andExpect(status().isOk()).andReturn();
        var formulario = org.jsoup.Jsoup.parse(erro.getResponse().getContentAsString());
        assertThat(formulario.selectFirst(".erro").text()).contains("já possui uma entrada aberta");

        var saidaBusca = mvc.perform(get("/veiculo/registrar-saida").session(admin).param("placa", "NOT2B34"))
                .andExpect(status().isOk()).andReturn();
        String id = org.jsoup.Jsoup.parse(saidaBusca.getResponse().getContentAsString())
                .selectFirst("#entradas-abertas input[name=id]").val();
        mvc.perform(post("/veiculo/registrar-saida").session(admin).with(csrf()).param("id", id))
                .andExpect(status().is3xxRedirection());
        var saidaSemConfirmacao = mvc.perform(get("/veiculo/registrar-saida").session(admin))
                .andExpect(status().isOk()).andReturn();
        assertThat(org.jsoup.Jsoup.parse(saidaSemConfirmacao.getResponse().getContentAsString())
                .select(".mensagem")).isEmpty();
    }

    @Test
    void capacidadeInvalidaOuAbaixoDaOcupacaoNaoSubstituiValorSalvo() throws Exception {
        var invalida = mvc.perform(post("/configuracoes").session(admin).with(csrf())
                        .param("capacidade", "abc"))
                .andExpect(status().isOk()).andReturn();
        var paginaInvalida = org.jsoup.Jsoup.parse(invalida.getResponse().getContentAsString());
        assertThat(paginaInvalida.selectFirst(".erro").text()).contains("capacidade válida");
        assertThat(paginaInvalida.selectFirst("#capacidade").val()).isEqualTo("abc");

        var zero = mvc.perform(post("/configuracoes").session(admin).with(csrf()).param("capacidade", "0"))
                .andExpect(status().isOk()).andReturn();
        assertThat(org.jsoup.Jsoup.parse(zero.getResponse().getContentAsString()).selectFirst(".erro").text())
                .contains("maior que zero");

        registrarEntrada("CAP1A23");
        registrarEntrada("CAP2B34");
        var abaixoDaOcupacao = mvc.perform(post("/configuracoes").session(admin).with(csrf())
                        .param("capacidade", "1"))
                .andExpect(status().isOk()).andReturn();
        var paginaErro = org.jsoup.Jsoup.parse(abaixoDaOcupacao.getResponse().getContentAsString());

        assertThat(paginaErro.selectFirst(".erro").text()).contains("menor que a ocupação atual");
        assertThat(paginaErro.selectFirst("#capacidade").val()).isEqualTo("1");
        assertThat(pagina("/configuracoes", admin).selectFirst("#capacidade").val()).isEqualTo("30");
    }

    @Test
    void configuracoesFicamRestritasAoAdmin() throws Exception {
        mvc.perform(cadastro("operador@example.com", "OPERADOR").session(admin))
                .andExpect(status().is3xxRedirection());
        var operador = login("operador@example.com", "SenhaDeTeste");

        mvc.perform(get("/configuracoes").session(operador)).andExpect(status().isForbidden());
        assertThat(pagina("/painel", operador).select("a[href='/configuracoes']")).isEmpty();
    }

    private void registrarEntrada(String placa) throws Exception {
        mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                        .param("placa", placa).param("modelo", "Veículo de teste").param("cor", "Prata"))
                .andExpect(status().isOk());
    }
}
