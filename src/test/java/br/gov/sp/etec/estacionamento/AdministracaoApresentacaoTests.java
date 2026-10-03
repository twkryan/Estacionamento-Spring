package br.gov.sp.etec.estacionamento;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdministracaoApresentacaoTests extends HttpTestSupport {
    @Test
    void primeiroCadastroMostraContextoPublicoSemEscolhaDePapel() throws Exception {
        var cadastro = pagina("/cadastro", new MockHttpSession());
        assertThat(cadastro.selectFirst("h1").text()).isEqualTo("Criar conta Admin");
        assertThat(cadastro.select("nav, select[name=papel]")).isEmpty();
        assertThat(cadastro.select("form[action='/efetuarCadastro'] input[name='_csrf']").val()).isNotBlank();
        assertThat(cadastro.select("a[href='/login']")).isNotEmpty();
    }

    @Test
    void erroDeCadastroInternoPreservaPapelEDadosMasNaoSenha() throws Exception {
        iniciarAdmin();
        var resposta = mvc.perform(cadastro("admin@example.com", "ADMIN").session(admin))
                .andExpect(status().isOk()).andReturn().getResponse();
        var cadastro = Jsoup.parse(resposta.getContentAsString(StandardCharsets.UTF_8));
        assertThat(cadastro.select(".mensagem.erro[role=alert]")).hasSize(1);
        assertThat(cadastro.selectFirst("#inputEmailCadastro").val()).isEqualTo("admin@example.com");
        assertThat(cadastro.selectFirst("#papel option[selected]").val()).isEqualTo("ADMIN");
        assertThat(cadastro.selectFirst("#inputSenhaCadastro").val()).isEmpty();
        assertThat(cadastro.select("a[href='/usuarios']")).isNotEmpty();
        assertThat(cadastro.select("nav a[aria-current=page]").attr("href")).isEqualTo("/usuarios");
    }

    @Test
    void acessoNegadoMantemStatusEOfereceRetornoParaOperacao() throws Exception {
        iniciarAdmin();
        mvc.perform(cadastro("operador@example.com", "OPERADOR").session(admin))
                .andExpect(status().is3xxRedirection());
        var operador = login("operador@example.com", "SenhaDeTeste");
        var resposta = mvc.perform(get("/acesso-negado").session(operador))
                .andExpect(status().isForbidden()).andReturn().getResponse();
        var pagina = Jsoup.parse(resposta.getContentAsString(StandardCharsets.UTF_8));
        assertThat(pagina.selectFirst("h1").text()).isEqualTo("Acesso negado");
        assertThat(pagina.select(".mensagem.erro[role=alert]").text()).contains("não tem permissão");
        assertThat(pagina.select("main a[href='/painel']")).hasSize(1);
        assertThat(pagina.select("nav a[href='/usuarios'], nav a[href='/configuracoes']")).isEmpty();
    }
}
