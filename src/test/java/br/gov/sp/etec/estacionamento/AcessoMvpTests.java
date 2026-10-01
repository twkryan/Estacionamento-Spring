package br.gov.sp.etec.estacionamento;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AcessoMvpTests extends HttpTestSupport {

    @Test
    void anonimoPrecisaEntrarAntesDeAcessarPainel() throws Exception {
        mvc.perform(get("/painel")).andExpect(status().is3xxRedirection());
    }

    @Test
    void primeiraContaCriaAdminECadastroSeguinteExigeAdmin() throws Exception {
        iniciarAdmin();
        assertThat(pagina("/usuarios", admin).text()).contains("ADMIN");
        mvc.perform(cadastro("intruso@example.com", "ADMIN")).andExpect(status().is3xxRedirection());
        mvc.perform(cadastro("operador@example.com", "OPERADOR").session(admin))
                .andExpect(status().is3xxRedirection());
        var operador = login("operador@example.com", "SenhaDeTeste");
        mvc.perform(get("/usuarios").session(operador)).andExpect(status().isForbidden());
        mvc.perform(get("/configuracoes").session(operador)).andExpect(status().isForbidden());
        mvc.perform(cadastro("outro@example.com", "ADMIN").session(operador)).andExpect(status().isForbidden());
        mvc.perform(get("/veiculo/registrar-entrada").session(operador)).andExpect(status().isOk());
        assertThat(pagina("/painel", operador).select("a[href='/usuarios']")).isEmpty();
        assertThat(pagina("/usuarios", admin).select("#usuarios tr")).hasSize(2);
    }

    @Test
    void loginInvalidoNaoGeraErroInternoELogoutEncerraSessao() throws Exception {
        iniciarAdmin();
        mvc.perform(post("/autenticar").with(csrf()).param("inputEmail", "inexistente@example.com")
                .param("inputSenha", "SenhaIncorreta")).andExpect(redirectedUrl("/?erro"));
        mvc.perform(post("/autenticar").with(csrf()).param("inputEmail", "admin@example.com")
                .param("inputSenha", "SenhaIncorreta")).andExpect(redirectedUrl("/?erro"));
        assertThat(pagina("/?erro", new org.springframework.mock.web.MockHttpSession()).text()).contains("Email ou senha inválidos");
        mvc.perform(post("/logout").session(admin).with(csrf())).andExpect(redirectedUrl("/?logout"));
        assertThat(pagina("/?logout", new org.springframework.mock.web.MockHttpSession()).select("p.mensagem").text())
                .contains("Sessão encerrada");
        mvc.perform(get("/painel")).andExpect(status().is3xxRedirection());
        assertThat(admin.isInvalid()).isTrue();
    }

    @Test
    void confirmacoesDeAutenticacaoRespeitamOpcaoDeNotificacoesMasErrosPermanecem() throws Exception {
        iniciarAdmin();
        var anonimo = new org.springframework.mock.web.MockHttpSession();
        assertThat(pagina("/?cadastro", anonimo).select("p.mensagem").text())
                .contains("Admin cadastrado");

        mvc.perform(post("/configuracoes/opcoes").session(admin).with(csrf())
                        .param("backup", "true").param("exportacao", "true"))
                .andExpect(status().is3xxRedirection());

        assertThat(pagina("/?logout", anonimo).select("p.mensagem")).isEmpty();
        assertThat(pagina("/?cadastro", anonimo).select("p.mensagem")).isEmpty();
        assertThat(pagina("/?erro", anonimo).select(".erro").text())
                .contains("Email ou senha inválidos");
        assertThat(pagina("/usuarios?sucesso", admin).select("p.mensagem")).isEmpty();
    }

    @Test
    void alteracaoSemCsrfNaoCriaPrimeiraConta() throws Exception {
        mvc.perform(post("/efetuarCadastro").param("inputEmailCadastro", "ataque@example.com"))
                .andExpect(status().isForbidden());
        assertThat(pagina("/", new org.springframework.mock.web.MockHttpSession()).text()).contains("Criar conta Admin");
    }

    @Test
    void adminEditaDadosPapelEAcessoMasNaoDesativaUltimoAdmin() throws Exception {
        iniciarAdmin();
        mvc.perform(cadastro("operador@example.com", "OPERADOR").session(admin)).andExpect(status().is3xxRedirection());
        var operador = login("operador@example.com", "SenhaDeTeste");
        String operadorId = pagina("/usuarios", admin).select("#usuarios tr").stream()
                .filter(row -> row.text().contains("operador@example.com")).findFirst().orElseThrow().attr("data-id");
        mvc.perform(editar(operadorId, "operador@example.com", "ADMIN", true).session(admin))
                .andExpect(redirectedUrl("/usuarios?sucesso"));
        assertThat(pagina("/usuarios?sucesso", admin).select("p.mensagem").text())
                .contains("Usuário salvo com sucesso");
        mvc.perform(get("/usuarios").session(operador)).andExpect(status().isOk());
        assertThat(pagina("/usuarios/" + operadorId, admin).selectFirst("#inputNomeCadastro").val()).isEqualTo("Nome editado");
        mvc.perform(editar(operadorId, "operador@example.com", "OPERADOR", true).session(admin))
                .andExpect(status().is3xxRedirection());
        mvc.perform(get("/usuarios").session(operador)).andExpect(status().isForbidden());
        mvc.perform(editar(operadorId, "operador@example.com", "OPERADOR", false).session(admin))
                .andExpect(status().is3xxRedirection());
        mvc.perform(get("/painel").session(operador)).andExpect(status().is3xxRedirection());
        mvc.perform(post("/autenticar").with(csrf()).param("inputEmail", "operador@example.com")
                .param("inputSenha", "SenhaDeTeste")).andExpect(redirectedUrl("/?erro"));
        String adminId = pagina("/usuarios", admin).select("#usuarios tr").stream()
                .filter(row -> row.text().contains("admin@example.com")).findFirst().orElseThrow().attr("data-id");
        var falha = mvc.perform(editar(adminId, "admin@example.com", "OPERADOR", false).session(admin))
                .andExpect(status().isOk()).andReturn();
        assertThat(falha.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8))
                .contains("Mantenha pelo menos um Admin ativo");
        assertThat(pagina("/usuarios", admin).text()).contains("ADMIN");
    }

    @Test
    void validacaoDoCadastroPreservaCamposENaoExpoeSenha() throws Exception {
        var response = mvc.perform(cadastro("teste@example.com", "ADMIN", "12", "2000-01-01"))
                .andExpect(status().isOk()).andReturn();
        var html = org.jsoup.Jsoup.parse(response.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
        assertThat(html.selectFirst("#inputEmailCadastro").val()).isEqualTo("teste@example.com");
        assertThat(html.selectFirst("#inputSenhaCadastro").val()).isEmpty();
        assertThat(html.text()).contains("CPF com 11 dígitos");
    }

    @Test
    void dataInvalidaVoltaAoFormularioComDadosPreservados() throws Exception {
        var response = mvc.perform(cadastro("teste@example.com", "ADMIN", "01234567890", "data-invalida"))
                .andExpect(status().isOk()).andReturn();
        var html = org.jsoup.Jsoup.parse(response.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
        assertThat(html.selectFirst("#inputEmailCadastro").val()).isEqualTo("teste@example.com");
        assertThat(html.selectFirst("#inputSenhaCadastro").val()).isEmpty();
        assertThat(html.text()).contains("data de nascimento válida");
    }

    @Test
    void cadastroComDataInvalidaDepoisDoBootstrapContinuaExigindoAdmin() throws Exception {
        iniciarAdmin();
        mvc.perform(cadastro("operador@example.com", "OPERADOR").session(admin)).andExpect(status().is3xxRedirection());
        var operador = login("operador@example.com", "SenhaDeTeste");
        mvc.perform(cadastro("intruso@example.com", "ADMIN", "01234567890", "data-invalida"))
                .andExpect(status().is3xxRedirection());
        mvc.perform(cadastro("intruso@example.com", "ADMIN", "01234567890", "data-invalida").session(operador))
                .andExpect(status().isForbidden());
        assertThat(pagina("/usuarios", admin).select("#usuarios tr")).hasSize(2);
    }

    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder editar(String id, String email, String papel, boolean ativo) {
        return post("/usuarios/" + id).with(csrf()).param("inputNomeCadastro", "Nome editado")
                .param("inputEmailCadastro", email).param("inputCPFCadastro", "01234567890")
                .param("inputTelefone", "11900000000").param("inputDataNascimentoCadastro", "2000-01-01")
                .param("papel", papel).param("ativo", Boolean.toString(ativo));
    }
}
