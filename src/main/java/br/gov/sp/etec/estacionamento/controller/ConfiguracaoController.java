package br.gov.sp.etec.estacionamento.controller;

import br.gov.sp.etec.estacionamento.service.ConfiguracaoService;
import br.gov.sp.etec.estacionamento.service.OperacaoInvalidaException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;

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

    @PostMapping("/opcoes")
    public String salvarOpcoes(@RequestParam(defaultValue = "false") boolean notificacoes,
                               @RequestParam(defaultValue = "false") boolean backup,
                               @RequestParam(defaultValue = "false") boolean exportacao,
                               RedirectAttributes redirect) {
        configuracoes.salvarOpcoes(notificacoes, backup, exportacao);
        redirect.addFlashAttribute("mensagem", "Opções atualizadas com sucesso.");
        return "redirect:/configuracoes";
    }

    @GetMapping("/backup")
    public Object baixarBackup(RedirectAttributes redirect) {
        if (!configuracoes.obter().backup()) {
            redirect.addFlashAttribute("erro", "Backup está desabilitado nas configurações.");
            return "redirect:/configuracoes";
        }
        byte[] sql = configuracoes.gerarBackupSql().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/sql;charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"estacionamento-backup.sql\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(sql);
    }

    private void prepararPagina(Model model, String capacidadeInformada) {
        var configuracao = configuracoes.obter();
        model.addAttribute("configuracao", configuracao);
        model.addAttribute("valorCapacidade", capacidadeInformada == null
                ? Integer.toString(configuracao.capacidade()) : capacidadeInformada);
        model.addAttribute("indicadores", configuracoes.indicadores());
    }
}
