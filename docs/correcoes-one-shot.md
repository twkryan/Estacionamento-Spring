# Correções do teste one shot

## Escopo aprovado

- Corrigir cadastro com telefone de 11 dígitos. CPF também é tratado como texto para aceitar 11 dígitos e preservar zeros à esquerda.
- Disponibilizar `/painel` para o botão Voltar e usar links absolutos de entrada e saída.
- Mostrar as entradas ainda abertas, permitir busca pela placa e confirmação da saída, registrar o horário e preservar o histórico. Não há cobrança nesta etapa.
- Testar os fluxos públicos HTTP de cadastro, login, painel e veículos usando H2 isolado. A revisão usa o commit `a6488c2` como referência.

## Critérios de aceitação

- Um cadastro com CPF e telefone de 11 dígitos pode autenticar com as credenciais cadastradas.
- Login inválido mostra a página de erro de credenciais.
- E-mail inexistente também recebe erro de credenciais, sem erro interno. O cadastro não registra senhas em logs.
- O botão Voltar alcança o painel. Seus links funcionam também após cadastrar um veículo.
- Entradas abertas aparecem na página de saída; a busca pela placa identifica o registro e permite confirmar sua saída.
- Uma saída confirmada deixa de aparecer entre as entradas abertas e permanece no histórico com entrada e saída.
- Repetir a confirmação não altera o horário já registrado. Identificadores inexistentes recebem uma mensagem compreensível.
- Nova entrada da mesma placa após a saída continua possível e representa outro registro.
- Compilação, testes automatizados, revisão e verificação no navegador são concluídos antes da entrega.
