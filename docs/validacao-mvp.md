# Validação da demonstração local

## Resultado

Conferência concluída em 02/10/2026 para a issue #8, na branch `codex/mvp-integracao`. Código integrado e verificado: `0043ccc15ac0c7452ca89996621c1485b176a6a8`. As alterações posteriores a esse commit são documentação. A demonstração usa dados sintéticos e banco próprio; o checkout principal e os bancos existentes foram preservados.

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

Execução, primeiro Admin e roteiro: [README](../README.md). A aplicação permanece local, sem cobrança, publicação, landing page ou troca de identidade visual. Não há pendência de implementação ou validação no escopo aprovado; a aceitação e o merge dos PRs ficam para o usuário.
