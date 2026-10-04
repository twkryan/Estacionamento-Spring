package br.gov.sp.etec.estacionamento;

import br.gov.sp.etec.estacionamento.service.ConfiguracaoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.datasource.url=jdbc:h2:mem:erros-servidor;DB_CLOSE_DELAY=-1")
class ErrosServidorTests {
    @Value("${local.server.port}") int porta;
    @MockitoSpyBean ConfiguracaoService configuracoes;

    @Test
    void recursoInexistenteUsaPaginaReal404SemConsultarConfiguracoes() throws Exception {
        clearInvocations(configuracoes);
        var resposta = consultar("/css/arquivo-inexistente-qa.css");

        assertThat(resposta.statusCode()).isEqualTo(404);
        assertThat(resposta.body()).contains("Página não encontrada", "Acessar sistema")
                .doesNotContain("Whitelabel", "NoResourceFoundException");
        verifyNoInteractions(configuracoes);
    }

    @Test
    void falhaDeConfiguracaoRenderizaErro500SemRepetirConsultaOuExporDetalhe() throws Exception {
        doThrow(new IllegalStateException("DETALHE_INTERNO_NAO_PUBLICAR"))
                .when(configuracoes).obter();
        clearInvocations(configuracoes);

        var resposta = consultar("/login");

        assertThat(resposta.statusCode()).isEqualTo(500);
        assertThat(resposta.body()).contains("Não foi possível concluir a solicitação", "Acessar sistema")
                .doesNotContain("DETALHE_INTERNO_NAO_PUBLICAR", "IllegalStateException", "Whitelabel");
        verify(configuracoes, times(1)).obter();
    }

    private HttpResponse<String> consultar(String caminho) throws Exception {
        try (var cliente = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build()) {
            return cliente.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + porta + caminho))
                    .timeout(Duration.ofSeconds(15)).header("Accept", "text/html").GET().build(),
                    HttpResponse.BodyHandlers.ofString(java.nio.charset.StandardCharsets.UTF_8));
        }
    }
}
