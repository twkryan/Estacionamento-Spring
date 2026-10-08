# Demonstração no Render

O serviço gratuito usa o `Dockerfile` da raiz para compilar com Java 25 e executar o pacote com o perfil `render`. A aplicação escuta em `0.0.0.0` na porta da variável `PORT` (padrão `10000`) e reconhece o HTTPS do proxy do Render. O processo usa um usuário sem privilégios e reserva parte da memória para a JVM e outras estruturas fora do heap.

## Configuração do serviço

- Tipo: Web Service.
- Repositório: `https://github.com/twkryan/Estacionamento-Spring`.
- Branch: `main`.
- Language/runtime: Docker.
- Dockerfile: `./Dockerfile`; contexto: `.`.
- Instância: Free.
- Health Check Path: `/`.
- Auto-deploy: habilitado.

Não é necessário informar comandos de build/start nem credenciais do banco. O Dockerfile define o perfil `render`, o fuso `America/Sao_Paulo` e as opções de memória. O serviço precisa ter acesso ao repositório privado pela integração GitHub do Render.

O `render.yaml` registra essa configuração e permite criar o serviço pelo [Blueprint no painel do Render](https://dashboard.render.com/blueprint/new?repo=https://github.com/twkryan/Estacionamento-Spring). Use esse caminho ou a criação direta de um Web Service, evitando criar dois serviços para a mesma demonstração.

## Dados da demonstração

O H2 fica em `/app/database/render-demo`. **O plano gratuito perde usuários, entradas, saídas e configurações a cada suspensão, reinício ou novo deploy.** O Render suspende o serviço após 15 minutos sem tráfego; o próximo acesso pode levar cerca de um minuto para iniciá-lo. Consulte as [limitações oficiais do plano gratuito](https://render.com/docs/free).

O banco local e arquivos pessoais não entram no contexto Docker. Uma implantação nova começa com base vazia. Abra `/login`, use **Criar conta Admin** e crie a conta inicial; depois entre com os dados cadastrados. Use somente dados fictícios nesta demonstração. O backup SQL manual existente pode ser baixado enquanto a instância estiver ativa; a interface não oferece restauração.

Para preservar o H2, é necessário contratar um serviço com disco persistente e configurar o caminho do banco dentro do volume. Essa contratação não faz parte da demonstração gratuita.

## Validação

Execute `./mvnw --batch-mode --no-transfer-progress verify` com JDK 25. Com Docker disponível, compile com `docker build -t estacionamento-render .` e execute com `docker run --rm -p 10000:10000 estacionamento-render`. Para testar autenticação por HTTP local, acrescente `-e SERVER_SERVLET_SESSION_COOKIE_SECURE=false`; no Render, mantenha cookies seguros.

Após a implantação, confirme status `live`, HTTP 200 em `/` e `/login`, redirecionamento de `/painel` para login sem sessão e ausência de erros de inicialização nos logs.
