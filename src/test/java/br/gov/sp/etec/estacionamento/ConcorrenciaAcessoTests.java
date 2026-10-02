package br.gov.sp.etec.estacionamento;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:acesso-concorrente;DB_CLOSE_DELAY=-1;LOCK_TIMEOUT=10000")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ConcorrenciaAcessoTests extends HttpTestSupport {
    @Autowired JdbcTemplate jdbc;
    @BeforeEach
    void baseNova() { jdbc.update("delete from tb_usuario"); }

    @Test
    void doisCadastrosPublicosSimultaneosCriamSomenteUmAdmin() throws Exception {
        var largada = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var primeiro = executor.submit(() -> { largada.await(); return mvc.perform(cadastro("primeiro@example.com", "ADMIN")).andReturn(); });
            var segundo = executor.submit(() -> { largada.await(); return mvc.perform(cadastro("segundo@example.com", "ADMIN")).andReturn(); });
            largada.countDown();
            var a = primeiro.get(15, TimeUnit.SECONDS);
            var b = segundo.get(15, TimeUnit.SECONDS);
            long criados = java.util.stream.Stream.of(a,b)
                    .filter(r -> "/?cadastro".equals(r.getResponse().getRedirectedUrl())).count();
            assertThat(criados).isEqualTo(1);
            String email = "/?cadastro".equals(a.getResponse().getRedirectedUrl()) ? "primeiro@example.com" : "segundo@example.com";
            var vencedor = login(email, "SenhaDeTeste");
            assertThat(pagina("/usuarios", vencedor).select("#usuarios tr")).hasSize(1);
            assertThat(pagina("/usuarios", vencedor).text()).contains("ADMIN");
        }
    }

    @Test
    void desativacoesSimultaneasNaoRemovemUltimoAdminAtivo() throws Exception {
        iniciarAdmin();
        mvc.perform(cadastro("segundo@example.com", "ADMIN").session(admin)).andExpect(status().is3xxRedirection());
        var segundo = login("segundo@example.com", "SenhaDeTeste");
        var rows = pagina("/usuarios", admin).select("#usuarios tr");
        String aId = rows.stream().filter(r -> r.text().contains("admin@example.com")).findFirst().orElseThrow().attr("data-id");
        String bId = rows.stream().filter(r -> r.text().contains("segundo@example.com")).findFirst().orElseThrow().attr("data-id");
        var largada = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var a = executor.submit(() -> { largada.await(); return mvc.perform(desativar(aId,"admin@example.com").session(admin)).andReturn(); });
            var b = executor.submit(() -> { largada.await(); return mvc.perform(desativar(bId,"segundo@example.com").session(segundo)).andReturn(); });
            largada.countDown();
            var respostas = java.util.List.of(a.get(15,TimeUnit.SECONDS), b.get(15,TimeUnit.SECONDS));
            assertThat(respostas.stream().filter(r -> "/usuarios?sucesso".equals(r.getResponse().getRedirectedUrl())).count()).isEqualTo(1);
            var tentativa = mvc.perform(post("/autenticar").with(csrf()).param("inputEmail","admin@example.com").param("inputSenha","SenhaDeTeste")).andReturn();
            var restante = "/painel".equals(tentativa.getResponse().getRedirectedUrl())
                    ? (org.springframework.mock.web.MockHttpSession) tentativa.getRequest().getSession(false)
                    : login("segundo@example.com","SenhaDeTeste");
            assertThat(pagina("/usuarios",restante).select("#usuarios tr").stream().filter(r -> r.select("td").get(3).text().equals("Ativo")).count()).isEqualTo(1);
        }
    }
    private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder desativar(String id, String email) {
        return post("/usuarios/"+id).with(csrf()).param("inputNomeCadastro","Usuário de teste")
                .param("inputEmailCadastro",email).param("inputCPFCadastro","01234567890")
                .param("inputTelefone","11900000000").param("inputDataNascimentoCadastro","2000-01-01")
                .param("papel","ADMIN").param("ativo","false");
    }
}
