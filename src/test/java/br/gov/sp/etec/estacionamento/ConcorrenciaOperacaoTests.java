package br.gov.sp.etec.estacionamento;

import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:operacao-concorrencia;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class ConcorrenciaOperacaoTests extends HttpTestSupport {
    @Test
    void concorrenciaNaoDuplicaPlacaNemUltrapassaCapacidade() throws Exception {
        iniciarAdmin();
        MockHttpSession segundaSessao = login("admin@example.com", "SenhaDeTeste");
        mvc.perform(post("/configuracoes").session(admin).with(csrf()).param("capacidade", "2"))
                .andExpect(status().is3xxRedirection());

        var duplicadas = concorrentes(
                () -> cadastrar(admin, "race-123", "Mesmo veículo"),
                () -> cadastrar(segundaSessao, "RACE 123", "Mesmo veículo"));
        assertThat(duplicadas).filteredOn(texto -> texto.contains("Entrada registrada com sucesso.")).hasSize(1);
        assertThat(duplicadas).filteredOn(texto -> texto.contains("Esta placa já possui uma entrada aberta.")).hasSize(1);
        assertThat(painel(admin).selectFirst("#ocupacao-atual").text()).isEqualTo("1");

        var entradasDiferentes = concorrentes(
                () -> cadastrar(admin, "LIM1A23", "Primeiro veículo"),
                () -> cadastrar(segundaSessao, "LIM2B34", "Segundo veículo"));
        assertThat(entradasDiferentes).filteredOn(texto -> texto.contains("Entrada registrada com sucesso.")).hasSize(1);
        assertThat(entradasDiferentes).filteredOn(texto -> texto.contains("Não há vagas disponíveis")).hasSize(1);

        var painelCheio = painel(admin);
        assertThat(painelCheio.selectFirst("#capacidade-atual").text()).isEqualTo("2");
        assertThat(painelCheio.selectFirst("#ocupacao-atual").text()).isEqualTo("2");
        assertThat(painelCheio.selectFirst("#vagas-disponiveis").text()).isEqualTo("0");
        assertThat(painelCheio.select("#painel-entradas tbody tr")).hasSize(2);
    }

    private String cadastrar(MockHttpSession session, String placa, String modelo) throws Exception {
        var response = mvc.perform(post("/veiculo/cadastrar").session(session).with(csrf())
                        .param("placa", placa).param("modelo", modelo).param("cor", "Prata"))
                .andExpect(status().isOk()).andReturn();
        return Jsoup.parse(response.getResponse().getContentAsString()).text();
    }

    private org.jsoup.nodes.Document painel(MockHttpSession session) throws Exception {
        var response = mvc.perform(get("/painel").session(session)).andExpect(status().isOk()).andReturn();
        return Jsoup.parse(response.getResponse().getContentAsString());
    }

    @SafeVarargs
    private final java.util.List<String> concorrentes(RequisicaoHttp... requisicoes) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(requisicoes.length);
        CountDownLatch prontos = new CountDownLatch(requisicoes.length);
        CountDownLatch iniciar = new CountDownLatch(1);
        try {
            var tarefas = java.util.Arrays.stream(requisicoes).map(requisicao -> pool.submit(() -> {
                prontos.countDown();
                if (!iniciar.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("Barreira não liberada.");
                return requisicao.executar();
            })).toList();
            assertThat(prontos.await(10, TimeUnit.SECONDS)).isTrue();
            iniciar.countDown();
            return tarefas.stream().map(tarefa -> {
                try {
                    return tarefa.get(20, TimeUnit.SECONDS);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).toList();
        } finally {
            pool.shutdownNow();
        }
    }

    @FunctionalInterface
    private interface RequisicaoHttp {
        String executar() throws Exception;
    }
}
