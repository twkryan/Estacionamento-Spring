package br.gov.sp.etec.estacionamento.service;
import br.gov.sp.etec.estacionamento.entity.*;
import br.gov.sp.etec.estacionamento.model.Usuario;
import br.gov.sp.etec.estacionamento.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarios;
    private final ConfiguracaoRepository configuracoes;
    private final PasswordEncoder senhas;
    public UsuarioService(UsuarioRepository usuarios, ConfiguracaoRepository configuracoes, PasswordEncoder senhas) {
        this.usuarios = usuarios; this.configuracoes = configuracoes; this.senhas = senhas;
    }
    public boolean baseNova() { return usuarios.count() == 0; }
    public boolean precisaMigrar() { return usuarios.existsByPapelIsNull(); }
    public List<UsuarioEntity> listar() { return usuarios.findAll(); }
    public UsuarioEntity buscar(Long id) {
        return usuarios.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
    }
    @Transactional
    public void cadastrar(Usuario dados, Papel papel, boolean admin) {
        configuracoes.bloquear();
        boolean primeiro = baseNova();
        if (!primeiro && !admin) throw new AccessDeniedException("Cadastro exige Admin.");
        var usuario = new UsuarioEntity();
        preencher(usuario, dados, true);
        usuario.setPapel(primeiro ? Papel.ADMIN : papel);
        usuario.setAtivo(true);
        usuarios.saveAndFlush(usuario);
    }
    @Transactional
    public void atualizar(Long id, Usuario dados, Papel papel, boolean ativo) {
        configuracoes.bloquear();
        var usuario = buscar(id);
        if (usuario.isAtivo() && usuario.getPapel() == Papel.ADMIN && (!ativo || papel != Papel.ADMIN)
                && usuarios.countByPapelAndAtivo(Papel.ADMIN, true) <= 1)
            throw new IllegalArgumentException("Mantenha pelo menos um Admin ativo.");
        preencher(usuario, dados, false);
        usuario.setPapel(papel); usuario.setAtivo(ativo);
        usuarios.saveAndFlush(usuario);
    }
    private void preencher(UsuarioEntity usuario, Usuario dados, boolean novo) {
        String nome = texto(dados.getInputNomeCadastro());
        String email = texto(dados.getInputEmailCadastro()).toLowerCase(Locale.ROOT);
        String cpf = texto(dados.getInputCPFCadastro());
        String telefone = texto(dados.getInputTelefone());
        String senha = dados.getInputSenhaCadastro();
        if (nome.isBlank() || nome.length() > 120 || !email.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+") || email.length() > 200)
            throw new IllegalArgumentException("Informe nome e email válidos.");
        if (!cpf.matches("\\d{11}") || !telefone.matches("\\d{10,11}"))
            throw new IllegalArgumentException("Informe CPF com 11 dígitos e telefone com 10 ou 11 dígitos.");
        if (dados.getInputDataNascimentoCadastro() == null || dados.getInputDataNascimentoCadastro().isAfter(java.time.LocalDate.now()))
            throw new IllegalArgumentException("Informe uma data de nascimento válida.");
        var existente = usuarios.findByInputEmailCadastroIgnoreCase(email);
        if (existente != null && !existente.getId().equals(usuario.getId()))
            throw new IllegalArgumentException("Este email já está cadastrado.");
        if (novo || (senha != null && !senha.isEmpty())) {
            if (senha == null || senha.length() < 8 || senha.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72)
                throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres e até 72 bytes.");
            usuario.setInputSenhaCadastro(senhas.encode(senha));
        }
        usuario.setInputNomeCadastro(nome); usuario.setInputEmailCadastro(email);
        usuario.setInputCPFCadastro(cpf); usuario.setInputTelefone(telefone);
        usuario.setInputDataNascimentoCadastro(dados.getInputDataNascimentoCadastro());
    }
    private String texto(String valor) { return valor == null ? "" : valor.trim(); }
}
