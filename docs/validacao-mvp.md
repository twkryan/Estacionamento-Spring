# Validação da demonstração local

## Estado

A validação final da issue #8 aguarda a integração das entregas #4, #5, #6 e #7. Este registro distingue a evidência já obtida da base dos resultados finais ainda pendentes.

## Base de acesso aprovada

- Commit: `4314aefdc7cccf61453494e76ed5b30bcebd5b99`, PR #9, issue #3 fechada.
- `mvnw.cmd package` com Java 25: 26 testes, zero falhas, erros ou ignorados; H2 isolado.
- Revisão Spec: zero achados restantes. Standards: zero violações documentais; uma observação P3 não bloqueante sobre verificação de Admin duplicada.
- Conferência no navegador integrado: cadastro, login, logout, gestão de usuários, acesso Admin/Operador, data de nascimento na edição e tabela em computador e celular.
- Essa evidência valida a base de acesso. Ainda não valida operação com vagas, consultas completas, downloads ou persistência do MVP integrado.

## Conferência final planejada

Usar a branch `codex/mvp-integracao`, Java 25, uma porta livre e um banco de demonstração próprio. Não encerrar instâncias de outros chats. Registrar o commit integrado e os comandos de validação efetivamente executados.

Conferir o ciclo completo em computador e celular: base vazia, primeiro Admin, Operador, configurações, entrada, painel, bloqueio de lotação e duplicidade normalizada, saída, histórico, nova visita, movimentações e relatórios filtrados. Comparar total e média, mantendo a ocupação atual independente do filtro. Conferir notificações habilitadas/desabilitadas e permanência de erros visíveis.

Conferir CSV filtrado, proteção de exportação desabilitada, backup exclusivo do Admin e bloqueado quando desabilitado. Validar o SQL em banco descartável, sem oferecer restauração na interface. Reiniciar com o mesmo banco e confirmar dados, papéis, opções e histórico.

Registrar abaixo os resultados finais, achados corrigidos, testes e compilação, evidências visuais e limitações materiais. A issue #8 só pode ser fechada após concluir essa conferência.
