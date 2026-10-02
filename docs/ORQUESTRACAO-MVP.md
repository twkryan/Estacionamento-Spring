# Coordenação da implementação

O usuário pediu explicitamente chats trabalhando em conjunto, com um orquestrador e implementadores. O escopo e as tarefas já aprovados continuam válidos. Não refazer planejamento, setup ou publicação de issues.

O usuário determinou que todos os implementadores usem GPT-6 Luna com esforço máximo (gpt-6-luna, max) e que todos os chats da equipe produzam um PR. Essa escolha explícita prevalece sobre recomendações de modelo de Skills. Não alterar Skills gerenciadas para acomodá-la.

## Papéis

| Papel | Chat | Modelo dos implementadores |
| --- | --- | --- |
| Orquestrador | 01a0f91c-315f-7973-8e4f-937197cbccda | Configuração padrão do chat |
| Implementador 1 — acesso | 01a0f912-e022-7a42-a6fa-f142faa44657 | gpt-6-luna, max |
| Implementador 2 — operação/backup | 01a0f91e-fa4f-7870-8487-7edc595947c2 | gpt-6-luna, max |
| Implementador 3 — consultas/relatórios | 01a0f91f-01f3-7100-9084-1505eb07c85d | gpt-6-luna, max |

- Orquestrador: acompanhar os chats, preservar contratos entre módulos, preparar worktrees isoladas, integrar commits, coordenar revisão e concluir a validação completa (#8). Produzir também seu próprio PR de integração final.
- Implementador 1, chat já existente 01a0f912-e022-7a42-a6fa-f142faa44657: concluir somente #3, Login, papéis e gestão de usuários. A #2 foi concluída e fechada; commit f0e3dc7 registra a transição explícita de credenciais legadas. Não avançar a outras tarefas após #3.
- Implementador 2: #4, Painel e controle de vagas; depois #7, Notificações e backup local.
- Implementador 3: #5, Movimentações e permanência; depois #6, Relatórios e exportação CSV.

## Inicialização segura

O Implementador 1 ainda trabalha na worktree C:\Users\twkryan\.codex\worktrees\0ef4\Estacionamento-Spring, branch codex/mvp-estacionamento. Enquanto ele conclui #3, os outros chats apenas leem e planejam: não alterar, fazer staging, commit, checkout, merge ou executar testes nessa worktree.

Após #3 estar concluída, revisada e commitada, sem alterações parciais de código ou staging pendente, o orquestrador prepara worktrees isoladas a partir desse commit. O único arquivo de coordenação não versionado, docs/ORQUESTRACAO-MVP.md, não bloqueia essa liberação e deve ser copiado para a worktree de integração. Usar create_worktree com referência explícita ao commit aprovado, reaproveitando attachments adequados se existirem, e informar os caminhos retornados aos implementadores. Os novos chats usam explicitamente esses caminhos nos comandos; não presumir que o cwd inicial do chat já tenha mudado.

Criar ou usar branches codex/mvp-operacao e codex/mvp-consultas nas respectivas worktrees, sem alterar a branch de outra worktree. Não usar o checkout principal D:\projetos\Estacionamento-Spring como destino de implementação.

Após a #3, criar também uma worktree isolada e a branch codex/mvp-integracao para o orquestrador. A branch codex/mvp-estacionamento permanece pertencendo ao Implementador 1 e ao seu PR. Copiar e versionar este documento na worktree de integração em momento seguro, sem alterar a branch do Implementador 1.

## Responsabilidade pelos módulos

- Implementador 1: autenticação, sessão, papéis, gestão de usuários, configurações de segurança, dependências necessárias e testes correspondentes. Preservar as interfaces que operação e consultas usarão.
- Implementador 2: capacidade, configurações persistentes, indicadores de vagas, painel, validação e registro de entrada, normalização de placas e proteção de concorrência; depois notificações e backup.
- Implementador 3: consultas de movimentações, permanência, filtros, totais/média, página de movimentações, relatórios e CSV. Preferir um módulo de consultas próprio, para não disputar o serviço de escrita e o repositório de operação.
- Orquestrador: contratos compartilhados, integração, documentação de andamento e validação final. Resolve pedidos de alteração em arquivos de outro responsável antes de permitir edições conflitantes.

Se dois módulos precisarem dos mesmos arquivos de menu, estilo, controller ou configuração, combinar o responsável e a interface antes de alterar. Todos devem assumir que não estão sozinhos no repositório e preservar mudanças de outros.

## Dependências reais

- #4 e #5 podem começar depois de #3.
- #7 pode começar depois de #4.
- #6 precisa de #4 e #5: antes de implementá-la, trazer para a worktree de consultas o estado aprovado de operação que fornece configurações e indicadores.
- #8 depende de #6 e #7 e é responsabilidade do orquestrador.

Não alterar bloqueios das issues para simular progresso. Não fechar tarefa sem seus critérios, testes e revisão.

## Comunicação e integração

O pedido humano direto de trabalhar em conjunto está no chat 01a0f8fd-fbc4-7d00-9fe3-4b136996ae1d. A troca de instruções e resultados entre os chats nomeados desta equipe serve exclusivamente a essa coordenação. A configuração das mensagens e dos IDs será entregue nos prompts de cada chat.

Os implementadores devem reportar somente ao orquestrador: issue, commit, arquivos alterados, interfaces, testes efetivamente executados, achados de revisão e bloqueios. Não trocar instruções diretamente entre implementadores nem avançar para fora de sua responsabilidade. O orquestrador deve preferir wait_threads com cursores para acompanhar progresso e enviar instruções apenas quando necessário.

Cada implementador faz staging explícito de seus arquivos e commits apenas em sua branch isolada. O Implementador 1 conclui o commit da #3 na branch base antes de sair. O orquestrador integra commits aprovados na branch codex/mvp-integracao, mantendo os commits de planejamento e os arquivos de configuração existentes e sem alterar a branch pertencente ao Implementador 1.

## PRs de cada participante

- Implementador 1: PR de codex/mvp-estacionamento para main, cobrindo #3 e a base de correções/planejamento incorporada.
- Implementador 2: PR de codex/mvp-operacao para codex/mvp-estacionamento, cobrindo #4 e #7.
- Implementador 3: PR de codex/mvp-consultas para codex/mvp-operacao, cobrindo #5 e #6. Antes de finalizar, incorporar a base de operação validada para manter o diff centrado nas consultas e relatórios.
- Orquestrador: PR de codex/mvp-integracao para main, cobrindo a integração final e #8.

Cada chat deve validar, commitar e publicar a própria branch, criar o PR e usar attach_artifact com a URL para vinculá-lo ao seu chat. Usar descrições concretas, com escopo e evidência de validação. Criar PRs draft enquanto aguardam integração ou aceitação final. Não fazer merge de PRs no GitHub sem instrução do usuário; integrar e revisar commits localmente continua sendo parte do trabalho autorizado.

Os implementadores são folhas de execução: não criar novos chats ou delegar implementação. As revisões em subagentes exigidas por code-review são coordenadas pelo orquestrador, que devolve os resultados a cada implementador antes do commit/PR final.

## Qualidade e ferramentas

Usar implement, tdd e code-review conforme a especificação. Reutilizar evidência suficiente para código e ambiente inalterados; testar novamente mudanças, falhas ou preocupações relevantes. As revisões exigidas por code-review podem usar subagentes nativos do Codex, sem Orca e sem acrescentar outra política de orquestração.

Os novos implementadores devem executar testes e builds em suas próprias worktrees, com H2 isolado. Usar Java 25 do runtime indicado em docs/HANDOFF-MVP.md. A validação visual final deve usar mcp__cua_repl e o navegador integrado do Codex, em uma instância própria e porta livre, sem encerrar servidores de outros chats.

## Documentos e fonte de verdade

- docs/HANDOFF-MVP.md: histórico, runtime e decisões anteriores.
- docs/especificacao-mvp.md: escopo aprovado.
- docs/github-mvp.json: mapa exato de issues e IDs.
- GitHub issue #1 e tarefas #2 a #8: estado atualizado do trabalho.
- Este documento será versionado pelo orquestrador em um momento seguro, depois da conclusão da #3, sem incluir alterações parciais de outro chat.

## Contratos alinhados após a #3

Base aprovada: `4314aefdc7cccf61453494e76ed5b30bcebd5b99`. Validação: 26 testes e compilação aprovados; revisão Spec sem achados restantes e Standards sem violações documentais (um julgamento P3 não bloqueante de duplicação da verificação de Admin).

- Operação assume `VeiculoService`, `VeiculoServiceImpl`, `VeiculoRepository`, `PainelController`, `PainelVeiculoController`, `Configuracao`, `ConfiguracaoRepository`, `Inicializacao`, `PaginaAdvice`, `fragments.html`, `app.css` e as telas de painel/entrada/saída/configurações, além de seus novos testes e backup.
- Consultas cria controllers, serviço, repositório de leitura e modelos próprios, telas de movimentações/relatórios, CSV e testes próprios. Não altera os arquivos de operação listados acima nem os de autenticação. CSS próprio pode ser criado se necessário.
- Operação fornece `model.Placa.normalizar(String)`, com caixa por `Locale.ROOT` e remoção de espaços e hífens, para comparar placas antigas e novas com a mesma regra, preservando a apresentação.
- `service.ConfiguracaoService.obter()` retorna `model.ConfiguracaoAtual(int capacidade, boolean notificacoes, boolean backup, boolean exportacao)`.
- `service.ConfiguracaoService.indicadores()` retorna `model.IndicadoresVagas(int capacidade, long ocupacao, long vagasDisponiveis, double percentualOcupacao)`; os indicadores ignoram filtros históricos.
- Os dois resultados são snapshots imutáveis. Gravação e bloqueio permanecem internos à operação. Entradas e alterações de capacidade validam e gravam sob o mesmo bloqueio pessimista da configuração singleton, dentro da transação.
- Consultas usa `/movimentacoes`, `/relatorios` e `/relatorios/exportar`. Operação usa `/configuracoes` e `/configuracoes/backup`; a proteção Admin de `/configuracoes/**` já existe na #3. Todos os formulários POST usam `th:action` para CSRF.
- Operação acrescenta ao menu compartilhado movimentações, relatórios e configurações (esta última só para Admin). Consultas #5 mantém a normalização extraível e só usa os contratos de configuração/indicadores na #6, após incorporar #4 aprovada.

Worktrees isoladas: `C:\Users\twkryan\.codex\worktrees\mvp-integracao\Estacionamento-Spring`, `C:\Users\twkryan\.codex\worktrees\mvp-operacao\Estacionamento-Spring` e `C:\Users\twkryan\.codex\worktrees\mvp-consultas\Estacionamento-Spring`. Comandos sempre usam explicitamente a worktree do responsável.
