# Login, papéis e gestão de usuários

## What to build

Permitir que o primeiro cadastro de uma base nova crie o Admin, autenticar com sessão e logout, proteger operações administrativas e permitir ao Admin gerenciar usuários e Operadores.

## Acceptance criteria

- [ ] Primeiro cadastro cria Admin; cadastros posteriores exigem Admin.
- [ ] Login, logout e erros de credenciais funcionam com sessão e senhas protegidas, sem senhas em logs.
- [ ] Usuários anônimos não acessam as páginas internas.
- [ ] Operador opera entradas e saídas e consulta dados, mas não administra usuários, configurações ou backup.
- [ ] Admin cadastra e edita usuários e papéis e pode desativar acesso, sem remover o último Admin ativo.
- [ ] A autorização é aplicada no servidor e na navegação.
- [ ] Conferir os fluxos HTTP e transições de acesso com H2 isolado; não promover usuários legados silenciosamente.

## Blocked by

- 1 — Integrar as correções existentes.
