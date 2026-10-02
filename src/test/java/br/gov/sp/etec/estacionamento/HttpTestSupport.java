package br.gov.sp.etec.estacionamento;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

abstract class HttpTestSupport {
    @Autowired MockMvc mvc;
    protected MockHttpSession admin;

    protected MockHttpServletRequestBuilder cadastro(String email, String papel) {
        return cadastro(email, papel, "01234567890", "2000-01-01");
    }
    protected MockHttpServletRequestBuilder cadastro(String email, String papel, String cpf, String nascimento) {
        return post("/efetuarCadastro").with(csrf())
                .param("inputNomeCadastro", "Usuário de teste")
                .param("inputCPFCadastro", cpf)
                .param("inputTelefone", "11900000000")
                .param("inputEmailCadastro", email).param("inputSenhaCadastro", "SenhaDeTeste")
                .param("inputDataNascimentoCadastro", nascimento).param("papel", papel);
    }
    protected void iniciarAdmin() throws Exception {
        mvc.perform(cadastro("admin@example.com", "OPERADOR")).andExpect(status().is3xxRedirection());
        admin = login("admin@example.com", "SenhaDeTeste");
    }
    protected MockHttpSession login(String email, String senha) throws Exception {
        var response = mvc.perform(post("/autenticar").with(csrf())
                .param("inputEmail", email).param("inputSenha", senha))
                .andExpect(redirectedUrl("/painel")).andReturn();
        return (MockHttpSession) response.getRequest().getSession(false);
    }
    protected org.jsoup.nodes.Document pagina(String rota, MockHttpSession session) throws Exception {
        return Jsoup.parse(mvc.perform(get(rota).session(session)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8));
    }
}
