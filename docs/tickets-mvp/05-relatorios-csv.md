# Relatórios e exportação CSV

## What to build

Consultar relatório filtrado com total e média de permanência, ver a ocupação atual e baixar o relatório em CSV quando o Admin habilitar a exportação.

## Acceptance criteria

- [ ] Página de relatórios possui busca por placa e período de entrada consistentes com movimentações.
- [ ] Total e média representam o conjunto filtrado; ocupação e vagas representam a situação atual.
- [ ] Barra ou indicador de ocupação usa os mesmos dados do painel.
- [ ] Admin salva a opção persistente de exportação em configurações.
- [ ] Admin e Operador podem exportar quando habilitado; desabilitado impede download no servidor.
- [ ] CSV respeita filtros e inclui dados e resumo apresentados, preservando acentos e tratando valores que possam ser interpretados como fórmulas.
- [ ] Validar consultas e conteúdo do download, estados vazios e acesso por fluxos HTTP.

## Blocked by

- 3 — Painel e controle de vagas.
- 4 — Movimentações e permanência.
