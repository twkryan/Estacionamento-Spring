# Issue tracker: GitHub

Issues e especificações deste projeto ficam no GitHub Issues.
Usar a CLI gh dentro do repositório.

Repositório: twkryan/Estacionamento-Spring.
Inferir o destino pelo remote origin.

## Convenções

- Criar: gh issue create --title "..." --body-file <arquivo>
- Ler: gh issue view <numero> --comments
- Listar: gh issue list --state open
- Comentar: gh issue comment <numero> --body-file <arquivo>
- Adicionar label: gh issue edit <numero> --add-label "..."
- Remover label: gh issue edit <numero> --remove-label "..."
- Fechar: gh issue close <numero> --comment "..."

Para textos com várias linhas, usar um arquivo com --body-file.

Quando uma Skill pedir para publicar no issue tracker,
criar uma issue no GitHub.

Quando pedir para buscar o ticket relevante,
ler a issue e seus comentários.

## Pull requests como entrada de triagem

**PRs as a request surface: no.**

## Wayfinding

- O mapa é uma issue com a label wayfinder:map.
- Tickets são issues vinculadas ao mapa como sub-issues.
- Se sub-issues não estiverem disponíveis, usar uma lista de
  tarefas no mapa e "Part of #<numero>" no ticket.
- Usar labels wayfinder:research, wayfinder:prototype,
  wayfinder:grilling ou wayfinder:task conforme o tipo.
- Registrar bloqueios com as dependências nativas do GitHub.
  Se indisponíveis, usar "Blocked by: #<numero>" no ticket.
- Um ticket fica disponível quando todos os bloqueadores
  estiverem fechados e não houver responsável atribuído.
- Selecionar o primeiro ticket disponível na ordem do mapa.
- Ao assumir, atribuir a issue com:
  gh issue edit <numero> --add-assignee @me
- Ao resolver, registrar a resposta, fechar a issue e atualizar
  as decisões do mapa com a referência ao resultado.
