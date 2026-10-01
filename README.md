# Estacionamento Spring

Aplicação de estacionamento com cadastro e login de usuários e registro de entrada de veículos, usando Spring Boot, Thymeleaf, Spring Data JPA e banco H2.

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

## Testes e compilação

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
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

Para salvar alterações:

```powershell
git add .
git commit -m "Descreva a alteração"
git push
```
