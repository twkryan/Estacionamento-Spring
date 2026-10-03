package br.gov.sp.etec.estacionamento;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NavegacaoOperacaoTests extends HttpTestSupport {
    @BeforeEach
    void autenticarAdmin() throws Exception { iniciarAdmin(); }

    @Test
    void paginasCompartilhadasIdentificamPaginaAtualELogoutProtegido() throws Exception {
        var rotas = Map.of(
                "/painel", "/painel",
                "/veiculo/registrar-entrada", "/veiculo/registrar-entrada",
                "/veiculo/registrar-saida", "/veiculo/registrar-saida",
                "/movimentacoes", "/movimentacoes",
                "/relatorios", "/relatorios",
                "/configuracoes", "/configuracoes",
                "/usuarios", "/usuarios",
                "/cadastro", "/usuarios");
        for (var rota : rotas.entrySet()) {
            var page = pagina(rota.getKey(), admin);
            var atual = page.select("nav [aria-current=page]");
            assertThat(atual).as(rota.getKey()).hasSize(1);
            assertThat(atual.first().attr("href")).isEqualTo(rota.getValue());
            assertThat(page.select("nav .grupo-nav > p").eachText())
                    .containsExactly("Operação", "Consultas", "Administração");
            var logout = page.selectFirst(".sessao form[action='/logout']");
            assertThat(logout.attr("method")).isEqualToIgnoringCase("post");
            assertThat(logout.selectFirst("input[name=_csrf]").val()).isNotBlank();
            assertThat(page.selectFirst(".sessao").text()).contains("Admin", "admin@example.com");
            assertThat(page.select("link[href='/css/acesso.css']")).isEmpty();
        }
        var editar = pagina("/usuarios", admin).selectFirst("#usuarios a").attr("href");
        assertThat(pagina(editar, admin).selectFirst("nav [aria-current=page]").attr("href"))
                .isEqualTo("/usuarios");
        mvc.perform(post("/logout").session(admin)).andExpect(status().isForbidden());
        mvc.perform(post("/logout").session(admin).with(csrf())).andExpect(status().is3xxRedirection());
    }

    @Test
    void operadorTemNavegacaoOperacionalSemAdministracao() throws Exception {
        mvc.perform(cadastro("operador@example.com", "OPERADOR").session(admin))
                .andExpect(status().is3xxRedirection());
        var operador = login("operador@example.com", "SenhaDeTeste");
        for (String rota : new String[]{"/painel", "/veiculo/registrar-entrada", "/veiculo/registrar-saida", "/movimentacoes", "/relatorios"}) {
            var page = pagina(rota, operador);
            assertThat(page.select("nav a").eachAttr("href"))
                    .containsExactly("/painel", "/veiculo/registrar-entrada", "/veiculo/registrar-saida", "/movimentacoes", "/relatorios");
            assertThat(page.selectFirst(".sessao").text()).contains("Operador", "operador@example.com");
            assertThat(page.select("nav [aria-current=page]")).hasSize(1);
        }
        mvc.perform(get("/usuarios").session(operador)).andExpect(status().isForbidden());
    }

    @Test
    void navegacaoIdentificaTemplateRetornadoDepoisDeEntradaValidaOuInvalida() throws Exception {
        var sucesso = mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                        .param("placa", "NAV1A23").param("modelo", "Modelo").param("cor", "Azul"))
                .andExpect(status().isOk()).andReturn();
        var painel = org.jsoup.Jsoup.parse(sucesso.getResponse().getContentAsString());
        assertThat(painel.selectFirst("nav [aria-current=page]").attr("href")).isEqualTo("/painel");
        assertThat(painel.selectFirst("#painel-entradas tbody a").attr("href"))
                .isEqualTo("/veiculo/registrar-saida?placa=NAV1A23");

        var falha = mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                        .param("placa", "NAV1A23").param("modelo", "Outro").param("cor", "Preto"))
                .andExpect(status().isOk()).andReturn();
        var entrada = org.jsoup.Jsoup.parse(falha.getResponse().getContentAsString());
        assertThat(entrada.selectFirst("nav [aria-current=page]").attr("href")).isEqualTo("/veiculo/registrar-entrada");
        assertThat(entrada.selectFirst("#placa").val()).isEqualTo("NAV1A23");
        assertThat(entrada.selectFirst(".erro").attr("role")).isEqualTo("alert");
    }

    @Test
    void lotacaoOrientaSaidaNoPainelENoFormularioSemAlterarEntradas() throws Exception {
        mvc.perform(post("/configuracoes").session(admin).with(csrf()).param("capacidade", "1"))
                .andExpect(status().is3xxRedirection());
        mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                        .param("placa", "LOT1A23").param("modelo", "Modelo").param("cor", "Azul"))
                .andExpect(status().isOk());
        var painel = pagina("/painel", admin);
        assertThat(painel.selectFirst("main .cabecalho-pagina .btn").attr("href"))
                .isEqualTo("/veiculo/registrar-saida");
        assertThat(painel.selectFirst(".situacao-patio").text()).contains("lotado");
        var entrada = pagina("/veiculo/registrar-entrada", admin);
        assertThat(entrada.selectFirst("#vagas-entrada").text()).isEqualTo("0");
        assertThat(entrada.selectFirst(".resumo-entrada a").attr("href"))
                .isEqualTo("/veiculo/registrar-saida");
        assertThat(painel.select("#painel-entradas tbody tr")).hasSize(1);
    }

    @Test
    void tabelasOperacionaisSaoRegioesAcessiveisEConfirmacaoIdentificaVisita() throws Exception {
        mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                        .param("placa", "TAB1A23").param("modelo", "Modelo").param("cor", "Azul"))
                .andExpect(status().isOk());
        for (String rota : new String[]{"/painel", "/veiculo/registrar-saida"}) {
            var page = pagina(rota, admin);
            for (var tabela : page.select(".tabela")) {
                assertThat(tabela.attr("role")).isEqualTo("region");
                assertThat(tabela.attr("tabindex")).isEqualTo("0");
                assertThat(tabela.attr("aria-label")).isNotBlank();
                assertThat(page.getElementById(tabela.attr("aria-describedby"))).isNotNull();
            }
        }
        var saida = pagina("/veiculo/registrar-saida", admin);
        var confirmacao = saida.selectFirst("#entradas-abertas button");
        assertThat(confirmacao.attr("aria-label")).contains("TAB1A23", "entrada em");
        assertThat(saida.selectFirst("#entradas-abertas input[name=id]").val()).isNotBlank();
        assertThat(saida.selectFirst("#entradas-abertas input[name=_csrf]").val()).isNotBlank();
    }
}
