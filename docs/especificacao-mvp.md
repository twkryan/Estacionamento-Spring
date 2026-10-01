# MVP do estacionamento local

Escopo, validação e divisão em tarefas aprovados pelo usuário em 01/10/2026. Publicado em https://github.com/twkryan/Estacionamento-Spring/issues/1.

## Problem Statement

O projeto registra entradas, mas ainda não oferece o conjunto de páginas e funcionalidades do site de referência. As correções já desenvolvidas em outra branch precisam ser aproveitadas para concluir uma demonstração local coerente, com dados reais do banco, histórico preservado e controle de acesso.

## Solution

Completar painel, entrada, saída, movimentações, relatórios e configurações. Manter o estilo escuro atual, adaptando a organização das telas. Disponibilizar login, papéis Admin e Operador, capacidade inicial de 30 vagas, permanência, filtros, notificações visuais, backup manual e exportação CSV. A aplicação continua local, sem cobrança e sem necessidade de publicação na internet.

## User Stories

1. Como primeiro usuário de uma base local nova, quero cadastrar o Admin para iniciar a operação.
2. Como usuário, quero autenticar e encerrar minha sessão para controlar meu acesso.
3. Como usuário, quero receber erro compreensível quando minhas credenciais forem inválidas.
4. Como Admin, quero cadastrar e gerenciar usuários e seus papéis para distribuir responsabilidades.
5. Como Operador, quero acessar a operação e as consultas sem poder alterar usuários ou configurações.
6. Como usuário autenticado, quero navegar entre as páginas permitidas com um menu consistente.
7. Como usuário autenticado, quero ver no painel as entradas abertas com placa, modelo, entrada e status.
8. Como usuário autenticado, quero ver capacidade, ocupação atual e vagas disponíveis.
9. Como Operador, quero registrar placa, modelo, cor e observações para identificar uma visita.
10. Como Operador, quero ser informado quando não houver vagas para evitar uma entrada inválida.
11. Como Operador, quero impedir uma segunda entrada aberta da mesma placa para manter a ocupação correta.
12. Como Operador, quero registrar nova visita da mesma placa depois de sua saída para preservar visitas distintas.
13. Como Operador, quero buscar uma entrada aberta por placa e confirmar sua saída.
14. Como Operador, quero que repetir a confirmação de saída preserve o horário já registrado.
15. Como Operador, quero ver a permanência em horas e minutos ao consultar uma visita.
16. Como usuário autenticado, quero ver movimentações abertas e encerradas sem excluir o histórico.
17. Como usuário autenticado, quero buscar movimentações por placa.
18. Como usuário autenticado, quero filtrar visitas pelo período de entrada.
19. Como usuário autenticado, quero ver o total de visitas incluídas na consulta.
20. Como usuário autenticado, quero ver a média de permanência apenas das visitas encerradas incluídas na consulta.
21. Como usuário autenticado, quero consultar relatórios com os filtros e os cálculos consistentes com movimentações.
22. Como usuário autenticado, quero ver a ocupação atual nos relatórios independentemente do período consultado.
23. Como Admin, quero alterar a capacidade sem deixá-la menor que a ocupação atual.
24. Como Admin, quero habilitar ou desabilitar notificações visuais de confirmação.
25. Como Admin, quero habilitar o backup e baixar uma cópia consistente dos dados locais.
26. Como Admin, quero habilitar ou desabilitar a exportação dos relatórios.
27. Como usuário autenticado, quero baixar o relatório consultado em CSV quando a exportação estiver habilitada.
28. Como usuário, quero que dados e configurações sejam preservados ao reiniciar a aplicação.
29. Como usuário, quero consultar mensagens claras e estados vazios quando não houver registros.
30. Como usuário, quero usar todas as telas mantendo o estilo atual em computador e celular.

## Implementation Decisions

### Base e persistência

- Aproveitar as correções do commit cfc890de9a4af71191f601f8c3b331b74ece1a1b, na branch codex/correcoes-one-shot, antes de modificar comportamentos que elas já cobrem.
- Manter Spring Boot, Thymeleaf, Spring Data JPA e H2 local. Usar Maven Wrapper e a versão Java definida no projeto.
- Preservar entradas existentes e seus horários. O modelo atual já representa uma entrada por linha; não exigir separação de entidades como pré-requisito.
- Não excluir uma entrada ao registrar saída. Encerrar apenas a entrada selecionada e manter a confirmação repetida sem alterar seu horário.
- Persistir capacidade e opções do sistema; inicializar uma base nova com 30 vagas e recursos habilitados, conforme a referência observada.

### Operação e consultas

- Normalizar placa para comparação, ignorando diferenças de caixa, espaços e hífen. Essa normalização deve ser usada tanto no bloqueio de duplicidade quanto nas consultas.
- Validar campos necessários no servidor e apresentar erros na própria tela, preservando dados preenchidos.
- Fazer a validação de capacidade e duplicidade junto ao registro da entrada, de forma que requisições simultâneas não produzam ocupação acima da capacidade ou duas entradas abertas da mesma placa.
- Apresentar permanência pela diferença dos horários completos, inclusive quando uma visita atravessar a meia-noite. Usar o horário local da demonstração e não cobrar.
- Filtro de período considera a data da entrada; limites informados são inclusivos. Permitir combinar período e placa. Rejeitar períodos invertidos.
- Contar cada entrada como uma visita. Média considera apenas visitas encerradas no conjunto filtrado; sem visitas encerradas, mostrar ausência de média, sem erro de cálculo.
- Indicadores de ocupação atual ignoram filtros históricos. Vagas disponíveis são capacidade menos entradas abertas; percentual usa a mesma base.

### Acesso e usuários

- Login com sessão, logout e autorização verificada no servidor. Ocultar links não substitui proteção das rotas.
- Armazenar novas senhas com hash apropriado e manter senhas fora dos logs. Definir a transição de credenciais locais antigas durante a integração, sem modificar silenciosamente usuários reais ou promover usuários existentes.
- Na base nova, apenas o primeiro cadastro público cria um Admin. Depois disso, cadastro e gestão de usuários exigem Admin.
- Admin pode operar, consultar e administrar; Operador pode operar e consultar, inclusive exportar quando a opção estiver habilitada.
- Gestão inclui cadastro, edição dos dados e do papel, e desativação de acesso. Preservar registros e impedir a remoção ou desativação do último Admin ativo.
- Se uma base antiga tiver usuários sem papéis, não promover automaticamente um usuário. Identificar e resolver a inicialização administrativa de forma explícita antes de usar essa base; uma demonstração com base nova segue o cadastro inicial definido acima.

### Configurações e recursos

- Configurações e backup são acessíveis apenas ao Admin. O Admin salva capacidade e opções com confirmação de sucesso ou erro.
- Notificações visuais controlam confirmações de operações; erros e validações continuam visíveis quando a opção estiver desabilitada.
- Backup é manual, por botão, e gera download de uma cópia consistente do banco local, incluindo dados e configurações. Preferir exportação SQL compatível com H2 a copiar um arquivo aberto.
- Exportação CSV é uma ação dos relatórios, respeita os filtros da consulta e inclui os dados e o resumo apresentados. Preservar caracteres portugueses e impedir que valores de entrada sejam executados como fórmulas ao abrir em planilhas.
- Desabilitar um recurso impede sua ação também no servidor; não apenas esconde o botão.
- Manter o estilo atual; compartilhar a navegação e os elementos comuns para evitar diferenças desnecessárias entre telas.

## Testing Decisions

- Usar como ponto principal os fluxos públicos HTTP já utilizados pelos testes da branch de correções, com H2 isolado do banco da demonstração.
- Testar comportamentos observáveis: resposta, redirecionamento, conteúdo da tela, autorização, persistência e downloads. Não fixar estrutura interna de serviços ou detalhes de implementação.
- Reaproveitar os testes existentes de cadastro, login, navegação, saída repetida, busca, histórico e compatibilidade com banco legado.
- Cobrir primeira conta Admin, cadastro subsequente, login, logout, acessos anônimos e tentativas de Operador em operações administrativas.
- Cobrir lotação, capacidade inválida, duplicidade normalizada, requisições simultâneas, saída e nova visita da mesma placa.
- Cobrir visitas abertas e encerradas, cruzamento de meia-noite, filtros, média sem registros encerrados e ocupação independente dos filtros.
- Cobrir salvamento das configurações, recurso desabilitado, CSV filtrado e backup contendo dados e configurações. Conferir a validade do backup em banco descartável sem disponibilizar restauração como funcionalidade do produto.
- Manter testes específicos menores apenas quando um comportamento importante, como concorrência ou geração consistente de backup, não puder ser verificado adequadamente no ponto HTTP.
- Ao final, executar testes e compilação e verificar os fluxos e o conteúdo renderizado no navegador integrado do Codex, em computador e celular. Registrar evidências atuais, sem tratar a inspeção de código como validação visual.

## Out of Scope

- Publicação na internet, domínio e infraestrutura de produção.
- Landing page e troca de identidade visual.
- Cobrança, pagamentos, tarifas e emissão de comprovantes financeiros.
- Notificações por e-mail, SMS ou outros serviços externos.
- Backup automático e restauração de backup na interface.
- Refatoração ampla do modelo sem necessidade para os comportamentos definidos.

## Further Notes

- Referência observada: https://estacionamento-facil.onrender.com/.
- A referência possui controles cujo comportamento não foi confirmado por submissão. Esta especificação define o comportamento aceito pelo usuário para as opções.
- Não há prazo ou exigências adicionais de avaliação.
- O setup já existe em outra worktree e na pasta principal; não repetir sua criação.
- A divisão proposta contém sete tarefas, com bloqueios apenas quando a entrega utiliza comportamento estabelecido por outra tarefa.
