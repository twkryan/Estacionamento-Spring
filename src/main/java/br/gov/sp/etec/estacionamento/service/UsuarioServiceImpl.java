package br.gov.sp.etec.estacionamento.service;

import br.gov.sp.etec.estacionamento.entity.UsuarioEntity;
import br.gov.sp.etec.estacionamento.model.Usuario;
import br.gov.sp.etec.estacionamento.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService{
    @Autowired
    UsuarioRepository repository;

    @Override
    public String cadastrarUsuario(Usuario usuario) {
        UsuarioEntity usuarioEntity = new UsuarioEntity();
        usuarioEntity.setInputNomeCadastro(usuario.getInputNomeCadastro());
        usuarioEntity.setInputCPFCadastro(usuario.getInputCPFCadastro());
        usuarioEntity.setInputTelefone(usuario.getInputTelefone());
        usuarioEntity.setInputSenhaCadastro(usuario.getInputSenhaCadastro());
        usuarioEntity.setInputDataNascimentoCadastro(usuario.getInputDataNascimentoCadastro());
        usuarioEntity.setInputEmailCadastro(usuario.getInputEmailCadastro());
        repository.save(usuarioEntity);

        return "Usuário cadastrado com sucesso!";
    }

    @Override
    public Usuario buscaUsuarioPorEmail(String email) {
        return toUsuario(repository.findByInputEmailCadastro(email));
    }

    private Usuario toUsuario(UsuarioEntity usuarioEntity){
        if (usuarioEntity == null) {
            return null;
        }
        Usuario usuario = new Usuario();
        usuario.setInputCPFCadastro(usuarioEntity.getInputCPFCadastro());
        usuario.setInputTelefone(usuarioEntity.getInputTelefone());
        usuario.setInputEmailCadastro(usuarioEntity.getInputEmailCadastro());
        usuario.setInputNomeCadastro(usuarioEntity.getInputNomeCadastro());
        usuario.setInputSenhaCadastro(usuarioEntity.getInputSenhaCadastro());
        usuario.setInputDataNascimentoCadastro(usuarioEntity.getInputDataNascimentoCadastro());
        return usuario;
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return List.of();
    }

    @Override
    public String atualizarUsuario(Usuario usuario) {
        return "";
    }

    @Override
    public String deletarUsuario(Long id) {
        return "";
    }
}
