# Documentação de domínio

## Antes de explorar o código

- Ler CONTEXT.md na raiz, quando existir.
- Ler os ADRs em docs/adr/ relacionados à área da tarefa.
- Se CONTEXT-MAP.md existir futuramente, seguir suas referências
  para os contextos relevantes.

Se esses documentos não existirem, seguir normalmente.
Não sugerir sua criação apenas por estarem ausentes.
A Skill domain-modeling os cria quando conceitos e decisões
forem efetivamente definidos.

## Estrutura

Este projeto usa single-context:

/
├── CONTEXT.md
├── docs/
│   └── adr/
└── src/

## Vocabulário

Usar os termos definidos em CONTEXT.md em issues, propostas,
hipóteses e nomes de testes.

Quando faltar um conceito, verificar se o termo corresponde
ao domínio e registrar a lacuna para domain-modeling.

## Conflitos com decisões

Se uma proposta contrariar um ADR existente, apontar o conflito
e justificar a revisão antes de substituir a decisão.
