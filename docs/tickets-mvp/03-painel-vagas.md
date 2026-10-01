# Painel e controle de vagas

## What to build

Mostrar entradas abertas e indicadores reais no painel, permitir ao Admin salvar a capacidade e impedir entradas inválidas por lotação ou duplicidade.

## Acceptance criteria

- [ ] Base nova começa com 30 vagas; capacidade é persistida e editável em configurações pelo Admin.
- [ ] Rejeitar capacidade inválida ou menor que a ocupação atual.
- [ ] Painel lista placa, modelo, entrada e status das entradas abertas e apresenta estado vazio compreensível.
- [ ] Ocupação, vagas disponíveis e total refletem o banco e são atualizados após entrada e saída.
- [ ] Validar campos no servidor e normalizar placa para comparação.
- [ ] Bloquear entrada quando cheio ou quando a placa já tiver entrada aberta; permitir nova visita após saída.
- [ ] Requisições simultâneas não ultrapassam capacidade nem criam entradas abertas duplicadas.
- [ ] Validar comportamento HTTP, persistência e concorrência, mantendo o estilo atual.

## Blocked by

- 2 — Login, papéis e gestão de usuários.
