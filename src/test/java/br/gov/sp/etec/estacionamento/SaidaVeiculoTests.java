package br.gov.sp.etec.estacionamento;

import org.junit.jupiter.api.Test;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SaidaVeiculoTests extends HttpTestSupport {
    @org.junit.jupiter.api.BeforeEach
    void autenticarAdmin() throws Exception { iniciarAdmin(); }

    @Test
    void formulariosDeSaidaMantemPostCsrfEStatusNeutroPorEntradaAberta() throws Exception {
        registrarEntrada("ENV1A23");
        registrarEntrada("ENV2B34");
        var page = paginaDeSaida("");
        var formularios = page.select("#entradas-abertas form[data-feedback-envio]");

        assertThat(formularios).hasSize(2);
        formularios.forEach(form -> {
            assertThat(form.attr("method")).isEqualTo("post");
            assertThat(form.attr("action")).isEqualTo("/veiculo/registrar-saida");
            assertThat(form.selectFirst("input[name=id]")).isNotNull();
            assertThat(form.selectFirst("input[name=_csrf]")).isNotNull();
            var status = form.selectFirst("[data-feedback-envio-status][role=status][hidden]");
            assertThat(status).isNotNull();
            assertThat(status.text()).isEmpty();
            assertThat(form.selectFirst("button[type=submit]").hasAttr("disabled")).isFalse();
        });
        assertThat(page.select("script[src='/js/feedback-envio.js']")).hasSize(1);
    }

    @Test
    void segundaConfirmacaoNaoAlteraHorarioDaSaida() throws Exception {
        registrarEntrada("REP4D56");
        String id = paginaDeSaida("REP4D56").selectFirst("input[name=id]").val();
        mvc.perform(post("/veiculo/registrar-saida").session(admin).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).param("id", id))
                .andExpect(status().is3xxRedirection());
        String saidaOriginal = paginaDeSaida("REP4D56")
                .select("#historico-saidas time").get(1).attr("datetime");

        mvc.perform(post("/veiculo/registrar-saida").session(admin).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).param("id", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("erro"));

        var page = paginaDeSaida("REP4D56");
        assertThat(page.select("#historico-saidas tr")).hasSize(1);
        assertThat(page.select("#historico-saidas time").get(1).attr("datetime"))
                .isEqualTo(saidaOriginal);
    }

    @Test
    void entradaInexistenteRecebeMensagemSemAlterarOutrasEntradas() throws Exception {
        registrarEntrada("VAL5E67");
        mvc.perform(post("/veiculo/registrar-saida").session(admin).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).param("id", "9223372036854775807"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("erro"));
        assertThat(paginaDeSaida("VAL5E67").select("#entradas-abertas tr")).hasSize(1);
    }

    @Test
    void mesmaPlacaPodeEntrarNovamenteSemPerderHistorico() throws Exception {
        registrarEntrada("NOV6F78");
        String id = paginaDeSaida("NOV6F78").selectFirst("input[name=id]").val();
        mvc.perform(post("/veiculo/registrar-saida").session(admin).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).param("id", id))
                .andExpect(status().is3xxRedirection());
        registrarEntrada("NOV6F78");

        var page = paginaDeSaida("NOV6F78");
        assertThat(page.select("#entradas-abertas tr")).hasSize(1);
        assertThat(page.select("#historico-saidas tr")).hasSize(1);
        assertThat(page.selectFirst("#entradas-abertas input[name=id]").val()).isNotEqualTo(id);
    }

    @Test
    void confirmarSaidaEncerraEntradaEPreservaOsHorariosNoHistorico() throws Exception {
        registrarEntrada("HST3C45");
        var abertas = paginaDeSaida("HST3C45");
        String id = abertas.selectFirst("#entradas-abertas input[name=id]").val();
        String horarioEntrada = abertas.selectFirst("#entradas-abertas time").attr("datetime");
        String entradaExibida = abertas.selectFirst("#entradas-abertas time").text();

        mvc.perform(post("/veiculo/registrar-saida").session(admin).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).param("id", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/veiculo/registrar-saida"));

        var encerradas = paginaDeSaida("HST3C45");
        assertThat(encerradas.select("#entradas-abertas tr")).isEmpty();
        assertThat(encerradas.select("#historico-saidas tr")).hasSize(1);
        assertThat(encerradas.selectFirst("#historico-saidas td").text()).isEqualTo("HST3C45");
        assertThat(encerradas.select("#historico-saidas time").get(0).text())
                .isEqualTo(entradaExibida);
        assertThat(java.time.LocalDateTime.parse(encerradas.select("#historico-saidas time").get(1).attr("datetime")))
                .isAfterOrEqualTo(java.time.LocalDateTime.parse(horarioEntrada));
    }

    private org.jsoup.nodes.Document paginaDeSaida(String placa) throws Exception {
        var response = mvc.perform(get("/veiculo/registrar-saida").session(admin).param("placa", placa))
                .andExpect(status().isOk()).andReturn();
        return Jsoup.parse(response.getResponse().getContentAsString());
    }

    @Test
    void entradaRegistradaPodeSerLocalizadaPelaPlacaNaPaginaDeSaida() throws Exception {
        registrarEntrada("TST1A23");
        registrarEntrada("OUT2B34");

        var response = mvc.perform(get("/veiculo/registrar-saida").session(admin).param("placa", "tst1a23"))
                .andExpect(status().isOk())
                .andReturn();
        var page = Jsoup.parse(response.getResponse().getContentAsString());
        assertThat(page.select("#entradas-abertas tr")).hasSize(1);
        assertThat(page.select("#entradas-abertas td").first().text()).isEqualTo("TST1A23");
        assertThat(page.select("button").text()).contains("Confirmar saída");
    }

    private void registrarEntrada(String placa) throws Exception {
        mvc.perform(post("/veiculo/cadastrar").session(admin).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                        .param("placa", placa)
                        .param("modelo", "Veículo de teste")
                        .param("cor", "Prata")
                        .param("observacao", "Registro fictício"))
                .andExpect(status().isOk());
    }
}
