# Estacionamento

Controle das entradas e saídas de veículos no estacionamento.

## Language

**Entrada**:
Registro de uma chegada ao estacionamento, identificado pela placa e pelo horário de entrada. Uma mesma placa pode ter várias entradas ao longo do tempo.
_Avoid_: Cadastro do veículo como sinônimo de uma permanência.

**Entrada aberta**:
Entrada cujo horário de saída ainda não foi registrado.
_Avoid_: Veículo excluído ou removido.

**Saída**:
Encerramento de uma entrada específica, com registro do horário em que o veículo deixou o estacionamento.
_Avoid_: Exclusão do registro.

**Histórico de saídas**:
Conjunto de entradas encerradas que preservam os horários de entrada e saída.
_Avoid_: Lixeira.
