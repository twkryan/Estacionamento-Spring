package br.gov.sp.etec.estacionamento;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:legado-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:schema-legado.sql"
})
@AutoConfigureMockMvc
class CompatibilidadeBancoTests {
    @Autowired
    MockMvc mvc;

    @Test
    void bancoComCamposNumericosMantemLoginEEntradaAposAtualizacao() throws Exception {
        mvc.perform(post("/autenticar")
                        .param("inputEmail", "legado@example.com")
                        .param("inputSenha", "SenhaLegada"))
                .andExpect(status().isOk()).andExpect(view().name("painel"));

        mvc.perform(post("/efetuarCadastro")
                        .param("inputEmailCadastro", "novo-legado@example.com")
                        .param("inputCPFCadastro", "11144477735")
                        .param("inputTelefone", "11900000000")
                        .param("inputSenhaCadastro", "SenhaNova"))
                .andExpect(status().isOk());

        var response = mvc.perform(get("/veiculo/registrar-saida").param("placa", "LEG8H90"))
                .andExpect(status().isOk()).andReturn();
        var page = Jsoup.parse(response.getResponse().getContentAsString());
        assertThat(page.selectFirst("#entradas-abertas td").text()).isEqualTo("LEG8H90");
        assertThat(page.selectFirst("#entradas-abertas time").text()).isEqualTo("01/10/2026 09:00:00");
    }
}
