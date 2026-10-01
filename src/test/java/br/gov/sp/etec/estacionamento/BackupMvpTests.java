package br.gov.sp.etec.estacionamento;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import javax.sql.DataSource;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:backup-mvp-tests",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BackupMvpTests extends HttpTestSupport {
    @Autowired JdbcTemplate jdbc;
    @Autowired DataSource dataSource;

    @BeforeEach
    void autenticarAdmin() throws Exception { iniciarAdmin(); }

    @Test
    void backupSqlIncluiDadosEConfiguracoesEAbreEmBancoDescartavel() throws Exception {
        mvc.perform(post("/configuracoes/opcoes").session(admin).with(csrf())
                        .param("notificacoes", "true").param("backup", "true"))
                .andExpect(status().is3xxRedirection());
        registrarEntrada("BAK1A23");

        var response = mvc.perform(get("/configuracoes/backup").session(admin))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.containsString("filename=\"estacionamento-backup.sql\"")))
                .andExpect(content().contentTypeCompatibleWith("application/sql"))
                .andReturn();
        String sql = response.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
        assertThat(sql).contains("CONFIGURACAO", "TB_VEICULO", "BAK1A23");

        String url = "jdbc:h2:mem:backup-validation-" + UUID.randomUUID();
        try (var connection = DriverManager.getConnection(url, "sa", "")) {
            org.h2.tools.RunScript.execute(connection, new StringReader(sql));
            try (var statement = connection.createStatement();
                 var vehicles = statement.executeQuery("SELECT PLACA, MODELO FROM TB_VEICULO WHERE PLACA = 'BAK1A23'")) {
                assertThat(vehicles.next()).isTrue();
                assertThat(vehicles.getString("MODELO")).isEqualTo("Veículo de teste");
            }
            try (var statement = connection.createStatement();
                 var options = statement.executeQuery("SELECT NOTIFICACOES, BACKUP, EXPORTACAO FROM CONFIGURACAO")) {
                assertThat(options.next()).isTrue();
                assertThat(options.getBoolean("NOTIFICACOES")).isTrue();
                assertThat(options.getBoolean("BACKUP")).isTrue();
                assertThat(options.getBoolean("EXPORTACAO")).isFalse();
            }
        }
    }

    @Test
    void backupDesabilitadoOuSemPapelAdminNaoPodeSerBaixado() throws Exception {
        mvc.perform(post("/configuracoes/opcoes").session(admin).with(csrf())
                        .param("notificacoes", "true").param("exportacao", "true"))
                .andExpect(status().is3xxRedirection());

        var configuracoes = pagina("/configuracoes", admin);
        assertThat(configuracoes.select("a[href='/configuracoes/backup']")).isEmpty();
        mvc.perform(get("/configuracoes/backup").session(admin))
                .andExpect(status().is3xxRedirection());
        assertThat(pagina("/configuracoes", admin).selectFirst(".erro").text())
                .contains("Backup está desabilitado");

        mvc.perform(cadastro("operador@example.com", "OPERADOR").session(admin))
                .andExpect(status().is3xxRedirection());
        var operador = login("operador@example.com", "SenhaDeTeste");
        mvc.perform(get("/configuracoes/backup").session(operador)).andExpect(status().isForbidden());
        mvc.perform(get("/configuracoes/backup")).andExpect(status().is3xxRedirection());
    }

    @Test
    void backupMantemUmSnapshotConsistenteDuranteGravacoesConcorrentes() throws Exception {
        jdbc.execute("CREATE TABLE BACKUP_SLOW_DATA (ID INT PRIMARY KEY, PAYLOAD VARCHAR(1024))");
        jdbc.update("INSERT INTO BACKUP_SLOW_DATA SELECT X, RPAD('x', 1024, 'x') FROM SYSTEM_RANGE(1, 25000)");
        jdbc.execute("CREATE TABLE BACKUP_MARKER (VERSION INT NOT NULL)");
        jdbc.update("INSERT INTO BACKUP_MARKER VALUES (0)");

        var executor = Executors.newSingleThreadExecutor();
        try {
            var future = executor.submit(() -> mvc.perform(get("/configuracoes/backup").session(admin)).andReturn());
            boolean scriptSerializavelAtivo = aguardarScriptSerializavelAtivo();
            assertThat(scriptSerializavelAtivo)
                    .as("o comando SCRIPT deve estar ativo na conexão SERIALIZABLE antes das gravações concorrentes")
                    .isTrue();

            try (Connection connection = dataSource.getConnection()) {
                connection.setAutoCommit(false);
                try (var statement = connection.createStatement()) {
                    statement.executeUpdate("UPDATE CONFIGURACAO SET CAPACIDADE = 42, NOTIFICACOES = FALSE, BACKUP = FALSE, EXPORTACAO = FALSE WHERE ID = 1");
                    statement.executeUpdate("UPDATE TB_USUARIO SET INPUT_NOME_CADASTRO = 'Admin alterado' WHERE INPUT_EMAIL_CADASTRO = 'admin@example.com'");
                    statement.executeUpdate("UPDATE BACKUP_MARKER SET VERSION = 1");
                    connection.commit();
                } catch (Exception e) {
                    connection.rollback();
                    throw e;
                }
            }

            var response = future.get(30, TimeUnit.SECONDS);
            assertThat(response.getResponse().getStatus()).isEqualTo(200);
            String sql = response.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
            assertThat(sql).contains("BACKUP_SLOW_DATA", "BACKUP_MARKER");

            String url = "jdbc:h2:mem:backup-concurrent-" + UUID.randomUUID();
            try (var restored = DriverManager.getConnection(url, "sa", "")) {
                org.h2.tools.RunScript.execute(restored, new StringReader(sql));
                try (var statement = restored.createStatement();
                     var configuration = statement.executeQuery("SELECT CAPACIDADE, NOTIFICACOES, BACKUP, EXPORTACAO FROM CONFIGURACAO")) {
                    assertThat(configuration.next()).isTrue();
                    assertThat(configuration.getInt("CAPACIDADE")).isEqualTo(30);
                    assertThat(configuration.getBoolean("NOTIFICACOES")).isTrue();
                    assertThat(configuration.getBoolean("BACKUP")).isTrue();
                    assertThat(configuration.getBoolean("EXPORTACAO")).isTrue();
                }
                try (var statement = restored.createStatement();
                     var user = statement.executeQuery("SELECT INPUT_NOME_CADASTRO FROM TB_USUARIO WHERE INPUT_EMAIL_CADASTRO = 'admin@example.com'")) {
                    assertThat(user.next()).isTrue();
                    assertThat(user.getString("INPUT_NOME_CADASTRO")).isEqualTo("Usuário de teste");
                }
                try (var statement = restored.createStatement();
                     var marker = statement.executeQuery("SELECT VERSION FROM BACKUP_MARKER")) {
                    assertThat(marker.next()).isTrue();
                    assertThat(marker.getInt("VERSION")).isZero();
                }
            }
        } finally {
            executor.shutdownNow();
        }
    }

    private boolean aguardarScriptSerializavelAtivo() throws Exception {
        long prazo = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        while (System.nanoTime() < prazo) {
            var niveis = jdbc.query(
                    "SELECT ISOLATION_LEVEL FROM INFORMATION_SCHEMA.SESSIONS " +
                            "WHERE EXECUTING_STATEMENT IS NOT NULL " +
                            "AND UPPER(EXECUTING_STATEMENT) LIKE '%SCRIPT SIMPLE COLUMNS NOPASSWORDS%'",
                    (resultado, linha) -> resultado.getString(1));
            if (niveis.stream().anyMatch("SERIALIZABLE"::equalsIgnoreCase)) {
                return true;
            }
            Thread.sleep(2);
        }
        return false;
    }

    private void registrarEntrada(String placa) throws Exception {
        mvc.perform(post("/veiculo/cadastrar").session(admin).with(csrf())
                        .param("placa", placa).param("modelo", "Veículo de teste").param("cor", "Prata"))
                .andExpect(status().isOk());
    }
}
