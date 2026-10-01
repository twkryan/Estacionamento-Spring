# Integrar as correções existentes

## What to build

Aproveitar a branch codex/correcoes-one-shot no commit cfc890de9a4af71191f601f8c3b331b74ece1a1b para disponibilizar cadastro e login corrigidos, navegação do painel, entrada, saída e histórico preservado na base do MVP, sem reimplementar esses comportamentos.

## Acceptance criteria

- [ ] Incorporar o trabalho existente preservando o planejamento e as alterações de outras atividades.
- [ ] Cadastro aceita CPF e telefone de 11 dígitos; credenciais inválidas ou inexistentes não geram erro interno.
- [ ] Rotas e navegação do painel, entrada e saída funcionam.
- [ ] Confirmar saída encerra a entrada selecionada e preserva o histórico; repetição mantém o horário original.
- [ ] Uma nova visita após a saída recebe registro próprio.
- [ ] Reaproveitar e executar os testes existentes com banco isolado, incluindo compatibilidade de dados.
- [ ] Resolver explicitamente a estratégia para credenciais e usuários locais legados antes de migrar qualquer base preexistente; a demonstração com base nova permanece suportada.

## Blocked by

None — can start immediately.
