package br.gov.sp.etec.estacionamento.config;
import br.gov.sp.etec.estacionamento.repository.UsuarioRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class SessaoAtualFilter extends OncePerRequestFilter {
    private final UsuarioRepository usuarios;
    public SessaoAtualFilter(UsuarioRepository usuarios) { this.usuarios = usuarios; }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var contexto = SecurityContextHolder.getContext();
        var auth = contexto.getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            var principal = auth.getPrincipal() instanceof UsuarioPrincipal usuarioPrincipal ? usuarioPrincipal : null;
            var usuario = principal == null ? null : usuarios.findById(principal.getUsuarioId()).orElse(null);
            if (usuario == null || !usuario.isAtivo() || usuario.getPapel() == null) {
                SecurityContextHolder.clearContext();
                if (request.getSession(false) != null) request.getSession(false).invalidate();
            } else {
                var principalAtualizado = new UsuarioPrincipal(usuario.getId(), usuario.getInputEmailCadastro(),
                        null, usuario.getPapel());
                contexto.setAuthentication(new UsernamePasswordAuthenticationToken(principalAtualizado, null,
                        principalAtualizado.getAuthorities()));
            }
        }
        chain.doFilter(request, response);
    }
}
