package br.gov.sp.etec.estacionamento.controller;

import br.gov.sp.etec.estacionamento.service.ConfiguracaoService;
import br.gov.sp.etec.estacionamento.service.OperacaoInvalidaException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/configuracoes")
public class ConfiguracaoController {
    private final ConfiguracaoService configuracoes;

    public ConfiguracaoController(ConfiguracaoService configuracoes) {
        this.configuracoes = configuracoes;
    }

    @GetMapping
    public String configuracoes(Model model) {
        prepararPagina(model, null);
        return "configuracoes";
    }

    @PostMapping
    public String salvarCapacidade(@RequestParam(defaultValue = "") String capacidade,
                                   Model model, RedirectAttributes redirect) {
        int valor;
        try {
            valor = Integer.parseInt(capacidade.trim());
        } catch (NumberFormatException e) {
            prepararPagina(model, capacidade);
            model.addAttribute("erro", "Informe uma capacidade válida em número inteiro.");
            return "configuracoes";
        }

        try {
            configuracoes.salvarCapacidade(valor);
            redirect.addFlashAttribute("mensagem", "Capacidade atualizada com sucesso.");
            return "redirect:/configuracoes";
        } catch (OperacaoInvalidaException e) {
            prepararPagina(model, capacidade);
            model.addAttribute("erro", e.getMessage());
            return "configuracoes";
        }
    }

    private void prepararPagina(Model model, String capacidadeInformada) {
        var configuracao = configuracoes.obter();
        model.addAttribute("configuracao", configuracao);
        model.addAttribute("valorCapacidade", capacidadeInformada == null
                ? Integer.toString(configuracao.capacidade()) : capacidadeInformada);
        model.addAttribute("indicadores", configuracoes.indicadores());
    }
}
