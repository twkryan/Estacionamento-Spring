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

**Movimentação**:
Uma visita ao estacionamento, apresentada com seu registro de entrada e, quando encerrada, seu registro de saída. Uma visita conta como uma movimentação, mesmo que ainda esteja aberta.
_Avoid_: Contar entrada e saída como duas visitas.

**Permanência**:
Tempo entre a entrada e a saída de uma visita encerrada, ou entre a entrada e o momento da consulta de uma visita aberta.
_Avoid_: Valor cobrado.

**Capacidade**:
Quantidade máxima de veículos que o estacionamento pode receber simultaneamente.
_Avoid_: Total de visitas.

**Ocupação atual**:
Quantidade de entradas abertas no momento da consulta.
_Avoid_: Quantidade de registros no histórico.

**Vagas disponíveis**:
Diferença entre a capacidade e a ocupação atual.

**Tempo médio de permanência**:
Média das permanências das visitas encerradas incluídas na consulta. Visitas abertas não compõem essa média.

**Admin**:
Usuário responsável por gerenciar os usuários, a capacidade e as configurações do estacionamento, além de operar e consultar as visitas.

**Operador**:
Usuário que registra entradas e saídas e consulta o painel, as movimentações e os relatórios.
