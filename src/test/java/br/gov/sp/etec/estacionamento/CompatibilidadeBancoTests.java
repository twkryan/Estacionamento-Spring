package br.gov.sp.etec.estacionamento;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:legado-test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.sql.init.mode=always",
        "spring.sql.init.schema-locations=classpath:schema-legado.sql"
})
@AutoConfigureMockMvc
@Transactional
class CompatibilidadeBancoTests extends HttpTestSupport {
    @Autowired JdbcTemplate jdbc;
    @Autowired PasswordEncoder senhas;

    @Test
    void contaLegadaNaoEPromovidaNemPermiteNovoCadastroPublico() throws Exception {
        mvc.perform(post("/autenticar").with(csrf()).param("inputEmail", "legado@example.com")
                .param("inputSenha", "SenhaLegada")).andExpect(redirectedUrl("/?erro"));
        assertThat(pagina("/", new org.springframework.mock.web.MockHttpSession()).text()).contains("migração administrativa");
        mvc.perform(cadastro("novo@example.com", "ADMIN")).andExpect(status().is3xxRedirection());
    }
    @Test
    void emailsDuplicadosLegadosRecusamLoginSemErroInterno() throws Exception {
        jdbc.update("insert into tb_usuario (input_email_cadastro,input_senha_cadastro) values ('legado@example.com','OutraSenha')");
        mvc.perform(post("/autenticar").with(csrf()).param("inputEmail", "legado@example.com")
                .param("inputSenha", "SenhaLegada")).andExpect(redirectedUrl("/?erro"));
    }
    @Test
    void migracaoExplicitaEmFixturePreservaCpfTelefoneEHorariosAntigos() throws Exception {
        // Fixture: representa a escolha offline explícita, nunca feita pela aplicação.
        jdbc.update("update tb_usuario set papel='ADMIN', ativo=true, input_senha_cadastro=? where input_email_cadastro='legado@example.com'",
                senhas.encode("SenhaNovaLegada"));
        admin = login("legado@example.com", "SenhaNovaLegada");
        var page = pagina("/veiculo/registrar-saida?placa=LEG8H90", admin);
        assertThat(page.selectFirst("#entradas-abertas td").text()).isEqualTo("LEG8H90");
        assertThat(page.selectFirst("#entradas-abertas time").text()).isEqualTo("01/10/2026 09:00:00");
        String id = pagina("/usuarios", admin).selectFirst("#usuarios tr").attr("data-id");
        var usuario = pagina("/usuarios/" + id, admin);
        assertThat(usuario.selectFirst("#inputCPFCadastro").val()).isEqualTo("1234567890");
        assertThat(usuario.selectFirst("#inputTelefone").val()).isEqualTo("900000000");
        mvc.perform(cadastro("novo-legado@example.com", "OPERADOR").session(admin)).andExpect(status().is3xxRedirection());
        login("novo-legado@example.com", "SenhaDeTeste");
    }
    @Test
    void cpfDaColunaGeradaPelaBaseDeCorrecoesTambemPermaneceLegivel() throws Exception {
        jdbc.update("update tb_usuario set inputcpfcadastro='01234567890', papel='ADMIN', ativo=true, input_senha_cadastro=? where input_email_cadastro='legado@example.com'",
                senhas.encode("SenhaNovaLegada"));
        admin = login("legado@example.com", "SenhaNovaLegada");
        String id = pagina("/usuarios", admin).selectFirst("#usuarios tr").attr("data-id");
        assertThat(pagina("/usuarios/" + id, admin).selectFirst("#inputCPFCadastro").val()).isEqualTo("01234567890");
    }
    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {"", "   "})
    void cpfPrincipalEmBrancoNaoEscondeDocumentoLegado(String cpf) throws Exception {
        jdbc.update("update tb_usuario set inputcpfcadastro=?, papel='ADMIN', ativo=true, input_senha_cadastro=? where input_email_cadastro='legado@example.com'",
                cpf, senhas.encode("SenhaNovaLegada"));
        admin = login("legado@example.com", "SenhaNovaLegada");
        String id = pagina("/usuarios", admin).selectFirst("#usuarios tr").attr("data-id");
        assertThat(pagina("/usuarios/" + id, admin).selectFirst("#inputCPFCadastro").val()).isEqualTo("1234567890");
    }
}
