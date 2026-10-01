# Notificações e backup local

## What to build

Permitir ao Admin controlar notificações visuais e habilitar backup manual, com download de uma cópia consistente dos dados e configurações locais.

## Acceptance criteria

- [ ] Admin salva as opções de notificações e backup; opções persistem após reinício.
- [ ] Notificações visuais confirmam operações quando habilitadas; erros continuam visíveis quando desabilitadas.
- [ ] Backup possui botão manual, funciona apenas quando habilitado e exige Admin no servidor.
- [ ] Download inclui dados e configurações e não copia um arquivo de banco em uso de forma inconsistente.
- [ ] Verificar conteúdo e validade do backup em banco descartável, sem alterar o banco da demonstração.
- [ ] Operador e usuários anônimos não baixam backup.
- [ ] Não adicionar restauração, serviços externos ou agendamento automático.

## Blocked by

- 3 — Painel e controle de vagas.
