# Movimentações e permanência

## What to build

Consultar todas as visitas, abertas e encerradas, com permanência, busca por placa, período de entrada, total de movimentações e tempo médio das visitas encerradas.

## Acceptance criteria

- [ ] Disponibilizar página de movimentações e acesso no menu para usuários autenticados.
- [ ] Mostrar placa, modelo, entrada, saída ou indicação de visita aberta, e permanência em horas e minutos.
- [ ] A permanência funciona também em visitas que atravessam a meia-noite.
- [ ] Busca por placa e período de entrada podem ser combinados; datas-limite inclusivas e período invertido tratado.
- [ ] Cada entrada conta uma visita; média usa apenas encerradas incluídas na consulta.
- [ ] Sem visitas encerradas, apresentar ausência de média sem erro; sem resultados, apresentar estado vazio.
- [ ] Compartilhar a definição das consultas e cálculos com relatórios, sem duplicar regras inconsistentes.
- [ ] Validar filtros, duração, conjuntos vazios e histórico preservado pelos fluxos HTTP.

## Blocked by

- 2 — Login, papéis e gestão de usuários.
