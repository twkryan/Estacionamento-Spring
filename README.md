# Estacionamento Spring

Aplicação local de estacionamento com Spring Boot, Thymeleaf, Spring Data JPA e banco H2. O MVP aprovado inclui acesso por Admin e Operador, entradas e saídas, controle de vagas, movimentações, relatórios, configurações e downloads locais. O andamento da implementação e da validação fica nas [issues do MVP](https://github.com/twkryan/Estacionamento-Spring/issues/1).

## Requisitos

- JDK 25, conforme definido no `pom.xml`.
- `JAVA_HOME` apontando para o JDK e Java disponível no `PATH`.
- Internet no primeiro uso para baixar o Maven e as dependências.

O Maven Wrapper está incluído no projeto; não é necessário instalar Maven separadamente.

## Executar no Windows

Abra o PowerShell na pasta do projeto:

```powershell
.\mvnw.cmd spring-boot:run
```

Acesse <http://localhost:8080>. Para encerrar, pressione `Ctrl+C` no terminal.

A aplicação escuta somente no computador local. Se a porta 8080 já estiver em uso, escolha uma porta livre sem encerrar a outra aplicação:

```powershell
.\mvnw.cmd spring-boot:run '-Dspring-boot.run.arguments=--server.port=8982'
```

Para uma demonstração com banco próprio, sem usar o banco padrão:

```powershell
.\mvnw.cmd spring-boot:run '-Dspring-boot.run.arguments=--server.port=8982 --spring.datasource.url=jdbc:h2:file:./database/demo'
```

Nesse caso, acesse <http://localhost:8982>. Reiniciar com o mesmo caminho de banco preserva os usuários, as visitas e as configurações.

## Primeiro acesso e papéis

Em uma base nova, abra **Criar conta Admin** na tela de login e preencha os dados solicitados. O primeiro cadastro cria o Admin; cadastros posteriores exigem uma sessão de Admin. Entre usando o e-mail e a senha cadastrados.

O Admin gerencia usuários e configurações e também opera e consulta. O Operador registra entradas e saídas e consulta o painel, as movimentações e os relatórios. A gestão permite editar dados e papéis ou desativar contas, preservando pelo menos um Admin ativo. Use **Sair** para encerrar a sessão.

Para dados de uma versão anterior, consulte [credenciais legadas](docs/credenciais-legadas.md). A aplicação não promove usuários antigos nem converte senhas em texto automaticamente; a demonstração pode usar uma base nova separada.

## Roteiro da demonstração do MVP

1. Crie o primeiro Admin e, em **Usuários**, cadastre um Operador.
2. Em **Configurações**, defina uma capacidade pequena para demonstrar a lotação; a capacidade não pode ficar abaixo da ocupação atual.
3. Registre entradas e confira capacidade, ocupação e vagas no painel. Tente repetir uma placa com diferenças de caixa, espaço ou hífen e tente uma entrada quando não houver vagas.
4. Registre a saída de uma visita. Confira a liberação da vaga, a permanência e o histórico; uma nova visita da mesma placa deve ser possível.
5. Consulte movimentações e relatórios por placa e período de entrada. O total conta visitas; a média considera somente as encerradas; a ocupação permanece atual, independente do filtro.
6. Confira notificações, CSV e backup nos botões próprios. Desabilite os recursos nas configurações e verifique que suas ações também ficam bloqueadas. Erros de validação continuam visíveis mesmo sem notificações de sucesso.
7. Entre como Operador e confira operação e consultas, incluindo CSV quando habilitado. Usuários, configurações e backup exigem Admin.
8. Reinicie usando o mesmo banco e confira dados, configurações e histórico. Confira também as páginas em uma tela de celular e em uma base vazia.

O backup é manual e baixa uma exportação SQL compatível com H2, com dados e configurações. A restauração não faz parte da interface. O CSV contém o conjunto filtrado e seu resumo; a exportação não é uma cobrança.

## Testes e compilação

```powershell
.\mvnw.cmd verify
```

`verify` executa a suíte de testes e gera o pacote. Os testes usam H2 isolado do banco local. O pacote gerado pode ser executado sem Maven:

```powershell
java -jar .\target\estacionamento-0.0.1-SNAPSHOT.jar
```

## Estrutura

- `src/main/java`: aplicação, controllers, entidades, modelos, repositórios e serviços.
- `src/main/resources/templates`: páginas Thymeleaf.
- `src/main/resources/application.properties`: configuração da aplicação e do banco.
- `src/test/java`: testes existentes.
- `database`: arquivos locais do H2, ignorados pelo Git.
- `.mvn`, `mvnw` e `mvnw.cmd`: Maven Wrapper.

O banco está configurado em `./database/appdb`. Os dados ficam no computador e não são enviados ao GitHub. Uma cópia nova do repositório começa sem os dados locais.

## GitHub

Repositório: <https://github.com/twkryan/Estacionamento-Spring>.

Escopo aprovado: [especificação do MVP](docs/especificacao-mvp.md). Coordenação e contratos: [orquestração](docs/ORQUESTRACAO-MVP.md). As entregas permanecem em PRs separados até a revisão da integração.

Resultado da demonstração: [validação do MVP](docs/validacao-mvp.md), com 57 testes aprovados, conferência em computador e celular, downloads e persistência após reinício.
