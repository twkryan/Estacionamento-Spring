package br.gov.sp.etec.estacionamento.config;

import br.gov.sp.etec.estacionamento.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }

    @Bean
    UserDetailsService usuarios(UsuarioRepository repository) {
        return email -> {
            if (repository.countByInputEmailCadastroIgnoreCase(email.trim()) != 1)
                throw new UsernameNotFoundException("Credenciais inválidas");
            var usuario = repository.findByInputEmailCadastroIgnoreCase(email.trim());
            if (usuario == null || usuario.getPapel() == null || !usuario.isAtivo()
                    || usuario.getInputSenhaCadastro() == null
                    || !usuario.getInputSenhaCadastro().matches("\\$2[aby]\\$12\\$.{53}")) {
                throw new UsernameNotFoundException("Credenciais inválidas");
            }
            return new UsuarioPrincipal(usuario.getId(), usuario.getInputEmailCadastro(),
                    usuario.getInputSenhaCadastro(), usuario.getPapel());
        };
    }

    @Bean
    SecurityFilterChain filtro(HttpSecurity http, UsuarioRepository usuarios) throws Exception {
        http.addFilterBefore(new SessaoAtualFilter(usuarios), org.springframework.security.web.access.intercept.AuthorizationFilter.class);
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/cadastro", "/efetuarCadastro", "/css/**", "/js/fluxo-ilustrado.js", "/error").permitAll()
                        .requestMatchers("/usuarios/**", "/configuracoes/**").hasRole("ADMIN")
                        .requestMatchers("/h2-console/**").denyAll()
                        .anyRequest().authenticated())
                .formLogin(login -> login.loginPage("/login").loginProcessingUrl("/autenticar")
                        .usernameParameter("inputEmail").passwordParameter("inputSenha")
                        .defaultSuccessUrl("/painel", true).failureUrl("/login?erro").permitAll())
                .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout"))
                .exceptionHandling(errors -> errors.accessDeniedPage("/acesso-negado"));
        return http.build();
    }
}
