# Divisão proposta do MVP

Granularidade e dependências aprovadas pelo usuário em 01/10/2026. As sete tarefas estão publicadas como issues #2 a #8, vinculadas à especificação #1 e com dependências nativas no GitHub. Cada tarefa tem seu corpo local em docs/tickets-mvp/; o mapeamento exato está em docs/github-mvp.json.

| Nº | Tarefa | Bloqueada por | Entrega verificável |
| --- | --- | --- | --- |
| 1 | Integrar as correções existentes | Nenhuma | Cadastro, navegação, entrada, saída e histórico preservado na base de trabalho |
| 2 | Login, papéis e gestão de usuários | 1 | Primeiro Admin, sessão, logout, Operador e gestão administrativa de usuários |
| 3 | Painel e controle de vagas | 2 | Painel com dados, capacidade editável e entradas protegidas contra lotação e duplicidade |
| 4 | Movimentações e permanência | 2 | Visitas abertas e encerradas, duração, busca, período e média consistente |
| 5 | Relatórios e exportação CSV | 3, 4 | Relatório filtrado, ocupação atual, opção de exportação e download CSV |
| 6 | Notificações e backup local | 3 | Opções persistentes, confirmações controladas e backup manual protegido |
| 7 | Validar a demonstração completa | 5, 6 | Fluxos integrados, telas verificadas em computador e celular e instruções atualizadas |

## Critério de divisão

Cada tarefa percorre o comportamento completo necessário, incluindo persistência, regras, acesso, tela e validação. Não separar trabalho apenas por camadas técnicas.

As tarefas 3 e 4 podem começar após a 2. A 5 usa os indicadores da 3 e as consultas da 4. A 6 usa a página e a persistência de configurações introduzidas pela 3. A 7 integra as entregas finais; os bloqueios transitivos não precisam ser repetidos.

Uma tarefa concluída deve ser demonstrável por si só. Os testes pertinentes acompanham cada tarefa, não são adiados integralmente à tarefa 7.
