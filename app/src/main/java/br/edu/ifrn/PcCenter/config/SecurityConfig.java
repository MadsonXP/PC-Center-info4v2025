package br.edu.ifrn.PcCenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Permite acesso público ao index, H2 console, CSS/JS (recursos estáticos)
                .requestMatchers(
                    "/", 
                    "/h2-console/**", 
                    "/css/**", "/js/**", "/images/**").permitAll()
                
                // Exige autenticação para todas as outras rotas (listagens e cadastros)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                // Define a URL da página de login customizada
                .loginPage("/login").permitAll()
                // URL para onde o usuário é redirecionado após o login bem-sucedido
                .defaultSuccessUrl("/pokemons", true)
            )
            .logout(logout -> logout
                // Define a URL de logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .permitAll()
            );

        // Configuração necessária para o H2 Console funcionar com Spring Security
        http.csrf(csrf -> csrf.disable());
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    // Bean que define o codificador de senhas a ser usado (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}