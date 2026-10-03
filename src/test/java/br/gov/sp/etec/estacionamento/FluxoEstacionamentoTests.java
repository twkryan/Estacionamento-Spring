package br.gov.sp.etec.estacionamento;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jsoup.Jsoup;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FluxoEstacionamentoTests extends HttpTestSupport {
    @BeforeEach
    void autenticarAdmin() throws Exception { iniciarAdmin(); }

    @Test
    void painelRetornadoAposRegistrarEntradaMantemNavegacaoValida() throws Exception {
        var response = mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                .param("placa", "NAV7G89").param("modelo", "Teste de navegação").param("cor", "Azul"))
                .andExpect(status().isOk()).andReturn();
        var page = Jsoup.parse(response.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
        assertThat(page.select("nav a[href='/veiculo/registrar-entrada']")).hasSize(1);
        assertThat(page.select("nav a[href='/veiculo/registrar-saida']")).hasSize(1);
        mvc.perform(get("/veiculo/registrar-entrada").session(admin)).andExpect(status().isOk());
        mvc.perform(get("/veiculo/registrar-saida").session(admin)).andExpect(status().isOk());
    }
    @Test
    void emailNaoCadastradoMostraErroDeCredenciaisSemFalhaInterna() throws Exception {
        mvc.perform(post("/autenticar").with(csrf()).param("inputEmail", "inexistente@example.com")
                .param("inputSenha", "SenhaIncorreta")).andExpect(redirectedUrl("/login?erro"));
        assertThat(pagina("/login?erro", new org.springframework.mock.web.MockHttpSession()).text()).contains("Email ou senha inválidos");
    }
    @Test
    void voltarAoPainelDisponibilizaLinksAbsolutosParaEntradaESaida() throws Exception {
        var page = pagina("/painel", admin);
        assertThat(page.select("nav a[href='/veiculo/registrar-entrada']")).hasSize(1);
        assertThat(page.select("nav a[href='/veiculo/registrar-saida']")).hasSize(1);
    }
    @Test
    void cadastroComCpfETelefoneDeOnzeDigitosPermiteLogin() throws Exception {
        mvc.perform(cadastro("regressao@example.com", "OPERADOR").session(admin)).andExpect(status().is3xxRedirection());
        var usuario = login("regressao@example.com", "SenhaDeTeste");
        assertThat(pagina("/painel", usuario).selectFirst("#titulo-painel").text()).isEqualTo("Painel");
        String id = pagina("/usuarios", admin).select("#usuarios tr").stream()
                .filter(row -> row.text().contains("regressao@example.com")).findFirst().orElseThrow().attr("data-id");
        var page = pagina("/usuarios/" + id, admin);
        assertThat(page.selectFirst("#inputCPFCadastro").val()).isEqualTo("01234567890");
        assertThat(page.selectFirst("#inputTelefone").val()).isEqualTo("11900000000");
        assertThat(page.selectFirst("#inputDataNascimentoCadastro").val()).isEqualTo("2000-01-01");
        assertThat(page.select("input[type=password]").attr("value")).isEmpty();
        mvc.perform(post("/autenticar").with(csrf()).param("inputEmail", "regressao@example.com")
                .param("inputSenha", "SenhaIncorreta")).andExpect(redirectedUrl("/login?erro"));
    }
}
