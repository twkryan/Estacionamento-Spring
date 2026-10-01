# Handoff: implementar o MVP do estacionamento

## Autorização e objetivo

O usuário solicitou o workflow da Skill ask-matt, iniciou grill-with-docs, respondeu às rodadas e aprovou expressamente o escopo consolidado, a validação e as sete tarefas. A especificação e as tarefas já foram publicadas. Em seguida, pediu um novo chat com contexto limpo para implementar. Continuar a implementação, sem refazer a entrevista ou o setup.

## Onde trabalhar

- Projeto salvo no Codex: Estacionamento, projectId a1eabc20-944a-4abc-82cb-683ceeaa2e32.
- Checkout principal: D:\projetos\Estacionamento-Spring. Não usar como destino das alterações desta implementação.
- Worktree preparada para a implementação: C:\Users\twkryan\.codex\worktrees\0ef4\Estacionamento-Spring.
- Branch: codex/mvp-estacionamento. Usar esta worktree e esta branch em todos os comandos de implementação.
- Ela já parte de cfc890de9a4af71191f601f8c3b331b74ece1a1b, correções prontas da branch codex/correcoes-one-shot. O planejamento foi salvo no commit 5cb8f2e, seguido pelo commit deste handoff.
- A worktree 5208 pertence ao trabalho anterior. Não alterar seus arquivos ou branch.

## Leitura inicial

Na worktree preparada, ler AGENTS.md, CONTEXT.md, docs/agents/domain.md, docs/agents/issue-tracker.md e docs/especificacao-mvp.md. A divisão está em docs/tarefas-mvp.md; corpos locais em docs/tickets-mvp/; identificadores reais em docs/github-mvp.json.

## Escopo confirmado

- Todas as páginas: painel, entrada, saída, movimentações, relatórios e configurações, além do acesso e cadastro de usuários necessários.
- Manter o estilo escuro atual e adaptar as telas. Landing page e mudança de estilo ficam para depois.
- Execução local para demonstração, sem prazo ou exigências adicionais.
- 30 vagas inicialmente; Admin altera capacidade sem reduzi-la abaixo da ocupação atual. Bloquear lotação e duas entradas abertas da mesma placa; nova visita após saída é permitida.
- Permanência em horas e minutos, sem cobrança.
- Movimentações abertas e encerradas, busca por placa e período da entrada. Total conta visitas, média considera só encerradas do conjunto filtrado. Ocupação é atual e independente dos filtros.
- Login obrigatório. Admin administra usuários e configurações; Operador opera e consulta. Primeiro cadastro de uma base nova cria Admin; depois só Admin cadastra usuários.
- Configurações habilitam notificações visuais, backup manual com download local e exportação CSV. Backup e exportação possuem botões próprios.
- Fora do escopo: publicação na internet, cobrança, restauração na interface, mensagens externas e backup automático.

## Issues e bloqueios

Especificação: https://github.com/twkryan/Estacionamento-Spring/issues/1.

| Issue | Entrega | Bloqueada por |
| --- | --- | --- |
| #2 | Integrar as correções existentes | Nenhuma |
| #3 | Login, papéis e gestão de usuários | #2 |
| #4 | Painel e controle de vagas | #3 |
| #5 | Movimentações e permanência | #3 |
| #6 | Relatórios e exportação CSV | #4, #5 |
| #7 | Notificações e backup local | #4 |
| #8 | Validar a demonstração completa | #6, #7 |

As oito issues usam ready-for-agent. As sete tarefas estão vinculadas à #1 como sub-issues e os bloqueios foram publicados pelas dependências nativas do GitHub. A lista de sub-issues e os bloqueios da #8 foram lidos de volta com sucesso. Não publicar duplicatas e não fechar ou modificar o corpo da issue pai.

## Estado real da implementação

A base de correções está incorporada ao iniciar a branch codex/mvp-estacionamento naquele commit. Ela cobre CPF/telefone como texto, erro de login inexistente, navegação, registro de entrada, confirmação de saída idempotente e histórico na própria tela de saída. O modelo já representa uma visita por registro, embora a entidade se chame VeiculoEntity; não exigir separação de entidades para começar.

Ainda faltam painel com dados, capacidade, duplicidade, permanência, página de movimentações, relatórios, autenticação com sessão/permissões, gestão de usuários e configurações. Nenhum código novo dessas funcionalidades foi escrito neste chat.

Antes de concluir a #2, revisar seus critérios e a estratégia explícita para credenciais/usuários legados. Não promover automaticamente um usuário existente a Admin nem alterar silenciosamente uma base real. A pasta database desta worktree contém apenas .gitkeep: a demonstração pode iniciar com uma base nova. Preservar as entradas e a compatibilidade do esquema antigo; a especificação detalha essa exigência.

## Evidência de validação já obtida

Nesta worktree, após partir das correções, executou-se .\mvnw.cmd test -q com Java 25. Resultado: 11 testes, zero falhas, zero erros, zero testes ignorados, nos quatro relatórios Surefire. Não repetir a mesma execução apenas por mudar de chat; repetir quando uma mudança, falha ou preocupação relevante justificar.

Essa evidência valida a base existente. Ela não prova as novas funcionalidades nem substitui a validação visual final do MVP. Os testes antigos públicos deverão acompanhar as novas regras de login e acesso, preservando as regressões de negócio.

## Runtime e ferramentas

Java já disponível em C:\Users\twkryan\AppData\Local\Codex\Runtimes\java25\jdk-25.0.4.1+1. Ele não está no PATH inicial desta sessão. Em cada processo PowerShell de build, definir apenas as variáveis do processo:

    $env:JAVA_HOME = 'C:\Users\twkryan\AppData\Local\Codex\Runtimes\java25\jdk-25.0.4.1+1'
    $env:PATH = (Join-Path $env:JAVA_HOME 'bin') + ';' + $env:PATH
    .\mvnw.cmd test

Não instalar Java, Maven ou mudar configurações globais. Maven Wrapper e dependências já estão disponíveis. GitHub CLI autenticada e GitHub Issues habilitado.

Há outros processos Java de demonstrações anteriores; não encerrá-los. Antes da validação visual, escolher uma porta livre e iniciar uma instância própria nesta worktree, sem tocar o banco das outras instâncias.

## Workflow a continuar

- Usar implement, em C:\Users\twkryan\.agents\skills\implement\SKILL.md, por tarefa e na ordem dos bloqueios.
- Usar tdd nos pontos aprovados: fluxos HTTP com H2 isolado; testes menores somente quando necessários para concorrência ou consistência de backup, conforme a especificação.
- Rever com code-review pelos eixos Standards e Spec. O usuário aprovou a base e o escopo; para a revisão completa, a comparação técnica de referência é a6488c2. Para cada nova tarefa, pinçar o commit anterior à implementação e deixar explícito o ponto usado.
- Skills do workflow estão instaladas em C:\Users\twkryan\.agents\skills. Não refazer setup-matt-pocock-skills: AGENTS.md e docs/agents/ foram reutilizados dos arquivos já existentes e estão versionados nesta branch.
- Usar Codex-native para colaboração quando uma Skill pedir agentes, sem Orca. Fora dessas exigências, não iniciar delegação automaticamente.
- Para browser e validação visual, usar mcp__cua_repl e o navegador integrado do Codex, com a documentação atual desse runtime. Não usar Playwright externo nem instruções sky antigas para substituí-lo.
- Especificação técnica ainda não implementada sugeriu Spring Security. Foram consultadas as páginas oficiais de form login e CSRF no Spring Security 7.1.1; nenhuma dependência de segurança foi adicionada ainda. Não presumir esse trabalho concluído.
- Salvar commits na branch de implementação após validação e revisão. Fechar cada tarefa apenas quando seus critérios forem atendidos; a #2 ainda está aberta. Não criar novo repositório, novo setup, spec duplicada ou novas tasks duplicadas.

## Comunicação

Responder em português. Prosseguir com as ações já aprovadas e evitar confirmações repetidas para decisões resolvidas. A implementação foi transferida por pedido do usuário para obter contexto limpo; não reabrir as perguntas de produto já aceitas.
