package br.gov.sp.etec.estacionamento.config;

import br.gov.sp.etec.estacionamento.entity.Papel;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public final class UsuarioPrincipal implements UserDetails, CredentialsContainer, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Long usuarioId;
    private final String username;
    private final Papel papel;
    private String password;

    public UsuarioPrincipal(Long usuarioId, String username, String password, Papel papel) {
        this.usuarioId = usuarioId;
        this.username = username;
        this.password = password;
        this.papel = papel;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + papel.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void eraseCredentials() {
        password = null;
    }
}
