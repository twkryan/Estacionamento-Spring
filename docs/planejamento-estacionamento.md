# Planejamento do estacionamento

Estado: decisões das duas rodadas aceitas; consolidação do escopo, validação e divisão das tarefas aguardando revisão final. Especificação e tarefas ainda não publicadas.

## Fontes

- Referência: https://estacionamento-facil.onrender.com/, páginas observadas em 01/10/2026.
- Base inicial desta worktree: commit a6488c2.
- Trabalho existente: branch codex/correcoes-one-shot, commit cfc890de9a4af71191f601f8c3b331b74ece1a1b, worktree 5208.
- Glossário: CONTEXT.md reutiliza as definições já registradas nessa branch.
- Setup existente: GitHub Issues de twkryan/Estacionamento-Spring, etiquetas padrão e documentação de domínio em contexto único. Não recriar o setup.

## Decisões confirmadas nesta entrevista

- A primeira entrega incluirá todas as páginas da referência, inclusive configurações.
- Manter o estilo visual atual e adaptar a organização e o conteúdo das telas.
- A aplicação será executada localmente para demonstração nesta entrega.
- Troca de estilo e desenvolvimento de landing page são possibilidades futuras, fora da entrega atual.
- Não há prazo definido nem exigências adicionais de avaliação.
- Capacidade inicial de 30 vagas, alterável pelo Admin sem reduzir abaixo da ocupação atual.
- Entrada bloqueada quando não houver vagas ou quando a mesma placa já tiver uma entrada aberta. Após a saída, uma nova visita da mesma placa continua possível.
- Permanência apresentada em horas e minutos, sem cobrança nesta entrega.
- Movimentações incluem visitas abertas e encerradas, com busca por placa e filtro pelo período da entrada.
- Total de movimentações conta visitas; a média usa apenas visitas encerradas incluídas na consulta. Ocupação permanece atual, independentemente dos filtros do histórico.
- Login obrigatório; Admin gerencia usuários, capacidade e configurações; Operador registra entradas e saídas e consulta as três páginas de consulta.
- O primeiro cadastro local cria o Admin; depois, apenas Admin cadastra usuários.
- Configurações habilitam notificações visuais na aplicação, backup manual para download local e exportação de relatórios em CSV. Backup e exportação possuem ações próprias.
- Restauração de backup, mensagens externas e backup automático ficam fora desta entrega.

## Páginas observadas na referência

| Página | Elementos observados |
| --- | --- |
| Painel | Lista de veículos presentes, horário de entrada, status e contadores de vagas ocupadas, disponíveis e totais |
| Entrada | Placa, modelo, cor e observações |
| Saída | Entradas abertas, horário, permanência e confirmação de saída |
| Movimentações | Entradas e saídas, duração, total de movimentações e tempo médio |
| Relatórios | Contadores de vagas, total de movimentações, tempo médio e barra de ocupação |
| Configurações | Usuários com permissões Admin e Operador; opções de notificações, backup e exportação |

A referência apresentou 30 vagas. Esse número foi adotado como capacidade inicial, editável pelo Admin.
Nas configurações não foi observada uma ação de salvar. Registros e alterações no site não foram executados; o funcionamento das opções ainda precisa ser definido.

## Trabalho existente e reaproveitamento

A inspeção da branch codex/correcoes-one-shot identificou correções de cadastro e login, rota do painel, navegação, registro de entrada, encerramento de entradas e histórico de saídas. O painel ainda não apresenta os dados do estacionamento. Não há controle de capacidade, impedimento de duas entradas abertas para a mesma placa ou cálculo de permanência.

Essas constatações são de leitura do código e da documentação; não representam uma nova execução de testes ou validação visual nesta entrevista. As mudanças não foram incorporadas a esta worktree.

O histórico existente fica na própria página de saída. Ainda não há página de movimentações nem de relatórios. O login ainda compara senhas diretamente, sem sessão autenticada, proteção de rotas, logout ou permissões.

O modelo existente registra uma entrada por linha, embora a entidade se chame VeiculoEntity. Separar veículo e movimentação em entidades distintas permanece uma alternativa de desenho; não é pré-requisito estabelecido para o MVP.

## Estado da entrevista

A primeira rodada foi concluída. O usuário aceitou as quatro recomendações da segunda rodada em 01/10/2026. O resumo, a validação proposta e a divisão em tarefas serão revisados antes da publicação.

## Próxima etapa

- Revisar o escopo consolidado e a validação pelos fluxos HTTP, com banco isolado e conferência visual pelo navegador integrado.
- Revisar a divisão em sete tarefas e suas dependências.
- Após essa revisão, publicar a especificação e as tarefas no GitHub Issues, com as relações de dependência.
- Integrar as correções existentes antes de implementar as lacunas, preservando o histórico e a compatibilidade dos dados.

Os detalhes técnicos na especificação são propostas de implementação do escopo aceito. Não tratar publicação ou implementação como já concluídas.
