package br.gov.sp.etec.estacionamento;

import org.junit.jupiter.api.Test;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FluxoEstacionamentoTests {
    @Autowired
    MockMvc mvc;

    @Test
    void painelRetornadoAposRegistrarEntradaMantemNavegacaoValida() throws Exception {
        var response = mvc.perform(post("/veiculo/cadastrar")
                        .param("placa", "NAV7G89")
                        .param("modelo", "Teste de navegação")
                        .param("cor", "Azul"))
                .andExpect(status().isOk()).andReturn();
        var page = Jsoup.parse(response.getResponse().getContentAsString());
        assertThat(page.select("a[href='/veiculo/registrar-entrada']")).hasSize(1);
        assertThat(page.select("a[href='/veiculo/registrar-saida']")).hasSize(1);
        mvc.perform(get("/veiculo/registrar-entrada")).andExpect(status().isOk());
        mvc.perform(get("/veiculo/registrar-saida")).andExpect(status().isOk());
    }

    @Test
    void emailNaoCadastradoMostraErroDeCredenciaisSemFalhaInterna() throws Exception {
        mvc.perform(post("/autenticar")
                        .param("inputEmail", "inexistente@example.com")
                        .param("inputSenha", "SenhaIncorreta"))
                .andExpect(status().isOk())
                .andExpect(view().name("erro"));
    }

    @Test
    void voltarAoPainelDisponibilizaLinksAbsolutosParaEntradaESaida() throws Exception {
        var response = mvc.perform(get("/painel"))
                .andExpect(status().isOk())
                .andExpect(view().name("painel"))
                .andReturn();
        var page = Jsoup.parse(response.getResponse().getContentAsString());
        assertThat(page.select("a[href='/veiculo/registrar-entrada']")).hasSize(1);
        assertThat(page.select("a[href='/veiculo/registrar-saida']")).hasSize(1);
    }

    @Test
    void cadastroComCpfETelefoneDeOnzeDigitosPermiteLogin() throws Exception {
        mvc.perform(post("/efetuarCadastro")
                        .param("inputNomeCadastro", "Teste de regressão")
                        .param("inputCPFCadastro", "01234567890")
                        .param("inputEmailCadastro", "regressao@example.com")
                        .param("inputSenhaCadastro", "SenhaDeTeste")
                        .param("inputDataNascimentoCadastro", "2000-01-01")
                        .param("inputTelefone", "11900000000"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));

        mvc.perform(post("/autenticar")
                        .param("inputEmail", "regressao@example.com")
                        .param("inputSenha", "SenhaDeTeste"))
                .andExpect(status().isOk())
                .andExpect(view().name("painel"));

        mvc.perform(post("/autenticar")
                        .param("inputEmail", "regressao@example.com")
                        .param("inputSenha", "SenhaIncorreta"))
                .andExpect(status().isOk())
                .andExpect(view().name("erro"));
    }
}
