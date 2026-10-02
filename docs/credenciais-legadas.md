# Transição explícita de bases antigas

A demonstração do MVP inicia em base nova, sem importar usuários reais. O primeiro cadastro nessa base cria o Admin. Entradas e horários existentes continuam compatíveis com o esquema anterior.

Uma base com usuários sem papel definido não permite cadastro público nem login desses usuários. A aplicação mostra a necessidade de migração administrativa. Nenhum usuário existente é promovido, nenhuma senha em texto é aceita como alternativa ao hash e nenhuma credencial é regravada automaticamente no login.

Para reutilizar uma base antiga, o responsável deve primeiro interromper a instância que a utiliza e conservar uma cópia recuperável. Validar a transição em uma cópia descartável antes de aplicar à base original. Inventariar os usuários, resolver emails duplicados sem apagar históricos, escolher expressamente quem será Admin e definir os demais como Operador ou inativos. Não inferir o Admin pelo menor identificador ou pela ordem de cadastro.

As contas aprovadas recebem novas senhas com BCrypt (custo 12), papel ADMIN ou OPERADOR e ativo=true. As demais ficam sem papel ou inativas e sem acesso. Gerar os hashes fora de logs e atualizar somente os identificadores expressamente selecionados, em transação offline. As colunas de entrada/saída e os identificadores das visitas não são alterados. Não distribuir a cópia do banco nem o backup SQL, pois contêm dados pessoais e hashes.

A leitura de CPF preserva as duas grafias encontradas nas bases anteriores: `inputcpfcadastro` gerada pelo mapeamento original e `input_cpf_cadastro` do esquema legado numérico. A primeira tem prioridade quando preenchida; a segunda é uma leitura de compatibilidade, sem copiar dados automaticamente. Novos cadastros usam a coluna do mapeamento original. Não acrescentar zeros a documentos antigos por inferência; a revisão dos dados faz parte da transição explícita.

Após a transição, conferir na cópia: login do Admin escolhido, login e restrições do Operador, bloqueio das contas não aprovadas, CPF/telefone preservados e horários das entradas existentes. Só então o responsável pode aplicar o mesmo procedimento à base real. Esta implementação não executa essa migração e não modifica bases externas à worktree.

Evidência da base integrada em cfc890d: 11 testes HTTP/compatibilidade passaram com H2 isolado e Java 25 na worktree preparada, antes deste handoff. Não houve alteração de código ao concluir a integração (#2); essa execução permanece válida para a base. Os testes passarão a exigir sessão e autorização na tarefa #3.
