package br.gov.sp.etec.estacionamento;

import br.gov.sp.etec.estacionamento.repository.UsuarioRepository;
import br.gov.sp.etec.estacionamento.service.ConfiguracaoService;
import br.gov.sp.etec.estacionamento.service.UsuarioService;
import br.gov.sp.etec.estacionamento.service.VeiculoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:acesso-redesign-test;DB_CLOSE_DELAY=-1")
@AutoConfigureMockMvc
@Transactional
class LandingAcessoTests extends HttpTestSupport {
    @MockitoSpyBean ConfiguracaoService configuracoes;
    @MockitoSpyBean UsuarioService usuarios;
    @MockitoSpyBean VeiculoService veiculos;
    @Autowired UsuarioRepository usuariosRepository;

    @Test
    void apresentacaoPublicaNaoConsultaServicosNemExibeDadosDeOperacao() throws Exception {
        iniciarAdmin();
        mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                .param("placa", "REA9L99").param("modelo", "Registro privado").param("cor", "Azul"))
                .andExpect(status().isOk());
        clearInvocations(configuracoes, usuarios, veiculos);

        var pagina = pagina("/", new MockHttpSession());

        verifyNoInteractions(configuracoes, usuarios, veiculos);
        assertThat(pagina.text()).contains("Dados demonstrativos", "exemplo");
        assertThat(pagina.text()).doesNotContain("REA9L99", "Registro privado", "admin@example.com");
        assertThat(pagina.select(".tabela-exemplo tbody tr")).hasSize(4);
        assertThat(pagina.select(".indicadores-exemplo dd").eachText()).containsExactly("30 vagas", "4 veículos", "26 vagas");
        assertThat(pagina.select("a.botao[href='/login']")).hasSize(1);
        assertThat(pagina.select("form")).isEmpty();
    }

    @Test
    void loginPublicoMantemFormularioRealCsrfEPrimeiroCadastro() throws Exception {
        var pagina = pagina("/login", new MockHttpSession());

        assertThat(pagina.select("form[method=post][action='/autenticar']")).hasSize(1);
        assertThat(pagina.select("input[name='_csrf']").attr("value")).isNotBlank();
        assertThat(pagina.select("#inputEmail").attr("autocomplete")).isEqualTo("username");
        assertThat(pagina.select("#inputSenha").attr("autocomplete")).isEqualTo("current-password");
        assertThat(pagina.select("a[href='/cadastro']")).hasSize(1);
        assertThat(pagina.select("link[rel=stylesheet]").attr("href")).isEqualTo("/css/acesso.css");
        assertThat(pagina.text()).doesNotContain("qualquer email", "Demonstração");
        mvc.perform(get("/css/acesso.css")).andExpect(status().isOk());
    }

    @Test
    void bootstrapRetornaAoNovoLoginEFechaCadastroPublico() throws Exception {
        assertThat(pagina("/cadastro", new MockHttpSession()).select("main a[href='/login']")).hasSize(1);
        mvc.perform(cadastro("admin@example.com", "OPERADOR"))
                .andExpect(redirectedUrl("/login?cadastro"));
        var login = pagina("/login?cadastro", new MockHttpSession());
        assertThat(login.select("a[href='/cadastro']")).isEmpty();
        assertThat(login.select("p.mensagem").text()).contains("Admin cadastrado");
        mvc.perform(get("/cadastro")).andExpect(redirectedUrl("/login"));
        mvc.perform(cadastro("intruso@example.com", "ADMIN"))
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void rotasProtegidasRetornamAoLoginELoginValidoVaiAoPainel() throws Exception {
        for (String rota : new String[]{"/painel", "/usuarios", "/configuracoes", "/movimentacoes", "/relatorios"}) {
            mvc.perform(get(rota)).andExpect(redirectedUrl("/login"));
        }
        iniciarAdmin();
        assertThat(pagina("/painel", admin).selectFirst("#titulo-painel").text()).isEqualTo("Painel");
        assertThat(pagina("/painel", admin).select("link[rel=stylesheet]").attr("href")).isEqualTo("/css/app.css");
        assertThat(pagina("/", admin).text()).doesNotContain("admin@example.com");
    }

    @Test
    void loginELogoutSemCsrfNaoExecutamOperacao() throws Exception {
        iniciarAdmin();
        mvc.perform(post("/autenticar").param("inputEmail", "admin@example.com").param("inputSenha", "SenhaDeTeste"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/logout").session(admin)).andExpect(status().isForbidden());
        mvc.perform(get("/painel").session(admin)).andExpect(status().isOk());
    }

    @Test
    void desativacaoInvalidaSessaoERecusaNovaAutenticacao() throws Exception {
        iniciarAdmin();
        var usuario = usuariosRepository.findByInputEmailCadastroIgnoreCase("admin@example.com");
        usuario.setAtivo(false);
        usuariosRepository.saveAndFlush(usuario);

        mvc.perform(get("/painel").session(admin)).andExpect(redirectedUrl("/login"));
        assertThat(admin.isInvalid()).isTrue();
        mvc.perform(post("/autenticar").with(csrf()).param("inputEmail", "admin@example.com")
                .param("inputSenha", "SenhaDeTeste")).andExpect(redirectedUrl("/login?erro"));
    }
}
