# Acesso, usuários e contratos da tarefa #3

## Comportamento entregue

- Uma base sem usuários permite `GET /cadastro` e `POST /efetuarCadastro`; o primeiro cadastro cria Admin independentemente do papel enviado. Depois, as duas rotas exigem Admin no servidor.
- Login: `GET /`, `POST /autenticar`, campos `inputEmail` e `inputSenha`. Sucesso redireciona para `/painel`; falha para `/?erro`. Logout: `POST /logout` com CSRF, redirecionando para `/?logout` e invalidando a sessão.
- Senhas novas usam BCrypt com custo 12. Não há autenticação alternativa com senha em texto nem promoção automática de usuários legados. O modelo não inclui senha em `toString()`; formulários de edição não devolvem hashes ou senhas.
- Admin acessa `GET /usuarios`, `GET /usuarios/{id}`, `POST /usuarios/{id}` e cadastro posterior. A edição permite dados pessoais, senha opcional, papel e desativação. A última conta Admin ativa não pode ser desativada ou perder o papel.
- Operador opera e consulta. `/usuarios/**` e `/configuracoes/**` exigem Admin, inclusive futuras ações de backup sob configurações. Demais páginas internas exigem autenticação. A navegação reflete o papel; permissões continuam verificadas no servidor.
- Papel e acesso ativo são conferidos na base a cada requisição autenticada, para que a sessão já aberta reflita alterações administrativas. CPF/telefone continuam texto; a compatibilidade das duas grafias antigas de CPF está em `credenciais-legadas.md`.

## Interfaces para as próximas tarefas

`Configuracao` é uma linha com id `1`, capacidade inicial `30` e notificações, backup e exportação habilitados. `Inicializacao` cria essa linha somente se ausente. Ela foi introduzida para serializar o primeiro cadastro e a preservação do último Admin com `ConfiguracaoRepository.bloquear()`, que exige uma transação e usa bloqueio pessimista no banco. A operação de capacidade/entradas pode usar o mesmo bloqueio, e consultas podem ler `findById(1L)`.

As funcionalidades de capacidade, opções, backup e CSV não estão implementadas nesta tarefa. O responsável pela operação assume `Configuracao`, seu repositório e inicialização a partir deste estado. Não adicionar uma segunda inicialização concorrente do singleton.

`fragments.html` fornece `head(titulo)`, `nav`, `mensagens` e `dadosUsuario`. `PaginaAdvice` disponibiliza `conectado`, `admin` e `emailAtual`. O menu já contém painel, entrada, saída e usuários; os implementadores posteriores acrescentam movimentações, relatórios e configurações ao mesmo fragmento, por coordenação do orquestrador. Estilo comum em `/css/app.css`, mantendo a paleta escura e roxa.

Formulários POST devem usar `th:action`, para que o Thymeleaf acrescente CSRF. Testes podem reutilizar `HttpTestSupport`: bootstrap e login reais pela interface HTTP, sem autenticação fictícia por anotação. A sessão `admin` é disponibilizada após `iniciarAdmin()`.

## Validação

Ponto de comparação da #3: `f0e3dc7`. A suite HTTP com H2 em memória mantém as regressões de cadastro, navegação, entradas, saída repetida, histórico e compatibilidade. Os novos testes cobrem bootstrap, login/logout, anonimato, autorização, mudanças de papel e desativação sobre sessões existentes, último Admin, CSRF e validação com campos preservados. Testes HTTP concorrentes usam banco próprio para duas criações iniciais e duas desativações simultâneas. A migração de credenciais legadas é apenas uma fixture explícita de teste; nenhuma base real é migrada pela aplicação.
