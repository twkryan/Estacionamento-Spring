package br.gov.sp.etec.estacionamento.service;

import br.gov.sp.etec.estacionamento.model.Usuario;

import java.util.List;

public interface UsuarioService {
    String cadastrarUsuario(Usuario usuario);
    List<Usuario> listarUsuarios();
    String atualizarUsuario(Usuario usuario);
    String deletarUsuario(Long id);
    Usuario buscaUsuarioPorEmail(String email);
}
