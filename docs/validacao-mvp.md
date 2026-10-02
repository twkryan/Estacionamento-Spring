# Validação da demonstração local

> **Limite histórico:** O registro original abaixo descreve a validação do snapshot `0043ccc15ac0c7452ca89996621c1485b176a6a8` e sua execução de 57 testes em 01/10/2026. Esses resultados permanecem como evidência daquele snapshot. A validação pós-review dos commits seguintes está em seção própria ao final deste documento.

## Resultado

Conferência original concluída em 02/10/2026 para a issue #8, na branch `codex/mvp-integracao`. Código integrado e verificado: `0043ccc15ac0c7452ca89996621c1485b176a6a8`. Naquele registro, alterações posteriores a esse commit eram documentação. A demonstração usa dados sintéticos e banco próprio; o checkout principal e os bancos existentes foram preservados.

## Testes e compilação

- Java 25, Maven Wrapper: `mvnw.cmd verify -q` concluído em 01/10/2026, após incorporar #6 e #7.
- 57 testes, zero falhas, erros ou ignorados. Pacote `target/estacionamento-0.0.1-SNAPSHOT.jar` gerado.
- Suíte HTTP/H2 cobre acesso, CSRF, último Admin, compatibilidade legada, capacidade, duplicidade e concorrência de entradas, saída repetida, permanência, filtros, CSV e backup.
- Backup concorrente: sessão do SCRIPT em SERIALIZABLE; alterações simultâneas de configuração, usuário e marcador; SQL restaurado mantém um único snapshot.
- Registro local: `target/verify-final.log` e `target/surefire-reports/`.

## Navegador integrado do Codex

Conferência por `mcp__cua_repl`, em computador (1280 px) e celular (390 × 844), incluindo leitura das páginas renderizadas e downloads pelos botões. Instância própria em `127.0.0.1:9020`, banco `./database/mvp-demo`, fuso `America/Sao_Paulo`.

| Fluxo | Resultado observado |
| --- | --- |
| Primeiro acesso | Base nova oferece Criar conta Admin; primeiro cadastro e login funcionam; o link público deixa de aparecer. |
| Contas e papéis | Admin cadastrou Operador; ambas as contas permanecem ativas. Operador recebe menu de operação/consultas e acesso negado em configurações e backup. |
| Login e logout | Credenciais inválidas apresentam erro compreensível; Sair encerra sessão. |
| Entrada e painel | Capacidade 2; duas entradas abertas mostram ocupação 2 e zero vagas. |
| Duplicidade | ABC-1D23 versus a bc1d23 é recusada; dados do formulário são preservados. |
| Lotação e capacidade | Terceira entrada e redução da capacidade para 1 são recusadas com erro e valores preservados. |
| Saída e histórico | Saída libera vaga e mantém registro; nova visita da mesma placa é aceita. Restam três visitas, duas abertas e uma encerrada. |
| Movimentações | Placa normalizada combinada com período inclusivo retorna duas visitas; período invertido mantém filtros e mostra erro. Média considera só a visita encerrada: 0h 02min. |
| Relatórios | Mesmo conjunto e média; ocupação atual 2/2 e zero vagas independem do filtro. Visita apenas aberta ou conjunto vazio mostra ausência de média. |
| Notificações | Desabilitar remove confirmação de sucesso; erro de capacidade continua visível. Reabilitar volta a exibir confirmação. |
| CSV | Download Admin filtrado contém duas visitas e resumo, com acentos. Operador também baixou CSV pelo celular. Desabilitado remove a ação; testes HTTP confirmam 403 no endpoint. |
| Backup | Admin baixou SQL; desabilitado redireciona com erro sem arquivo; Operador recebe Acesso negado. |
| Persistência | Reinício com mesmo banco manteve contas, capacidade 2, visitas/histórico e as três opções desabilitadas. Depois foram reabilitadas para a demonstração. |
| Celular | Formulários, navegação, painel, consultas, configurações, usuários e botão de saída conferidos. Tabelas usam rolagem no contêiner, sem alargar a página. Últimas colunas acessíveis por teclado. |

Uma segunda base própria, `./database/mvp-vazio` na porta 9021, confirmou primeiro Admin e estados sem visitas: painel 30/0/30, movimentações total zero/sem média e relatório com ocupação 0%. A instância e a aba temporária foram encerradas. As visitas abertas também foram consultadas após a meia-noite; a permanência em horas e minutos continuou coerente.

## Restauração descartável do download

O arquivo baixado pelo navegador foi copiado para `target/validacao-mvp/backup-demo.sql` e executado com `org.h2.tools.RunScript` (H2 2.4.240) em um novo banco sob `target/validacao-mvp/`. Consultas com `org.h2.tools.Shell` confirmaram:

- Capacidade 2; notificações, backup e exportação habilitados.
- Três visitas, uma encerrada.
- Uma conta Admin e uma conta Operador.

O banco original não foi usado como destino. A restauração continua fora da interface do produto.

## Revisões e correções

### Standards

Zero violações documentais e zero novos achados de integração em `a6488c2...56b4d5c`; o delta #7, `94bcb026...defd0e1`, também foi aprovado. O fragmento comum eliminou a duplicação de mensagens na saída. Restam três sugestões P3 não bloqueantes: verificação do papel Admin, preparação dos dados do painel e markup das tabelas de consultas duplicados.

### Spec

Zero achados restantes. As revisões dos módulos foram reutilizadas na integração, preservada sem conflitos. Os dois P2 de #7 foram corrigidos: confirmações fora do fragmento agora respeitam notificações; o backup usa SERIALIZABLE na conexão efetiva do SCRIPT e possui prova concorrente de restauração coerente.

A conferência móvel corrigiu a tabela de consultas comprimida: largura mínima 650 px, rolagem horizontal dentro do contêiner e regiões de consultas rotuladas/focáveis. Medição final: página 375/375 px, tabela 650 px dentro de contêiner 305 px; rolagem chegou a 345/345 px e exibiu saída/permanência.

## Evidências e entrega

Capturas e downloads locais em `target/validacao-mvp/` (ignorados pelo Git). Principais: `painel-final-desktop.jpg`, `movimentacoes-final-mobile.jpg`, `movimentacoes-final-mobile-direita.jpg`, `configuracoes-opcoes-mobile.jpg`, `opcoes-persistidas.jpg` e `relatorio-filtrado.csv`. `movimentacoes-mobile.jpg` é evidência anterior à correção, não aprovação final.

PRs draft: [acesso #9](https://github.com/twkryan/Estacionamento-Spring/pull/9), [operação/backup #11](https://github.com/twkryan/Estacionamento-Spring/pull/11), [consultas/relatórios #12](https://github.com/twkryan/Estacionamento-Spring/pull/12) e [integração #10](https://github.com/twkryan/Estacionamento-Spring/pull/10). Nenhum PR foi mesclado no GitHub.

Execução, primeiro Admin e roteiro: [README](../README.md). A aplicação permanece local, sem cobrança, publicação, landing page ou troca de identidade visual. Naquela validação, não havia pendência de implementação ou validação no escopo aprovado; a aceitação e o merge dos PRs ficaram para o usuário.

## Validação pós-review — 02/10/2026

Este registro complementa a validação original; não substitui seus resultados nem atribui seus 57 testes aos commits pós-review.

### Correções revisadas

| Achado | Commit e PR | Revisão e evidência |
| --- | --- | --- |
| P1 — a sessão identificava a conta pelo e-mail mutável; após trocar e reutilizar o endereço, uma sessão de Operador poderia assumir a conta Admin. | `e71d7bda5320d310555905bd03af7211bd2f68bd`, PR #9. | Identidade de sessão passa a usar o ID estável do usuário. Standards: zero achados; Spec: zero achados; 27 testes, sem falhas. |
| P2 — o menu da entrega de operação apontava para `/movimentacoes` e `/relatorios`, ainda sem rotas naquele módulo. | `6945e028287574cd91467ee0b908aee377879b3a`, PR #11. | O módulo de operação oculta os links enquanto as rotas não existem. Standards: zero achados; Spec: zero achados; 43 testes, sem falhas. |
| Integração da navegação — as duas rotas estão presentes em consultas. Os links foram restaurados e a regressão exige ambos no menu de `/painel`; cada anchor renderizado é consultado por HTTP para Admin e Operador. | Merge `7e8fa5b2347989a629f021189eb0083abc00264a`, PR #12; pais `3f9da3c5f9d9ceebba3c6270459be7e8b0147b0a` e `7bd06d8e706f603d9ad9f84ea90f4e1ec4e7e8a6`. | Standards: zero violações e smells; Spec: zero itens ausentes, fora de escopo ou incorretos. RED: o teste falhou pelas duas âncoras ausentes; GREEN: 11 testes, sem falhas. |

### Integração e suíte final

- Merge local `99afadbe0af6b617b1de292ec7264978ea88a18a`, pais `e70d1e1694fbe738098f91859fdcfcacce6d00f3` e `7e8fa5b2347989a629f021189eb0083abc00264a`. O merge foi limpo, sem conflitos; o código de aplicação e os testes ficaram idênticos ao commit aprovado de consultas.
- Java 25.0.4.1; `mvnw.cmd verify` executado em 02/10/2026. Resultado: 59 testes em 11 relatórios Surefire, zero falhas, erros ou skips. O pacote `target/estacionamento-0.0.1-SNAPSHOT.jar` foi gerado.
- Log local: `target/post-review-mvnw-verify.log`; relatórios: `target/surefire-reports/`.

### Navegador integrado

- Validação com `mcp__cua_repl` no navegador integrado do Codex, usando a aplicação do commit `99afadbe0af6b617b1de292ec7264978ea88a18a` em `127.0.0.1:9022` (processo Java PID 28564), fuso `America/Sao_Paulo` e banco H2 sintético novo `target/post-review-browser-20261002`.
- Admin e Operador autenticaram-se na mesma aba, em sequência. Em desktop e viewport móvel de 390 × 844, `/painel` exibiu a identidade da sessão e o menu correspondente ao papel; ambos os papéis abriram pelo menu `/movimentacoes` e `/relatorios` nos dois tamanhos.
- Capturas locais, ignoradas pelo Git: `target/post-review-evidence/admin-desktop-panel.jpg`, `admin-mobile-panel.jpg`, `operator-desktop-panel.jpg` e `operator-mobile-panel.jpg`. A aba permanece no painel em `http://127.0.0.1:9022/painel` com viewport padrão.

PRs #9, #10, #11 e #12 permanecem drafts; a aceitação e o merge ficam a critério do usuário. Nenhum PR foi mesclado no GitHub.
