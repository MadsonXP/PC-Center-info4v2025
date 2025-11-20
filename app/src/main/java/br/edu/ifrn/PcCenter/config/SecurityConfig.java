package br.edu.ifrn.PcCenter.config;

// ... imports existentes

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
                // 1. ROTAS RESTRITAS AO ADMIN (Apenas Ash pode acessar)
                .requestMatchers("/treinadores", "/treinadores/**").hasRole("ADMIN")
                
                // 2. ROTAS PÚBLICAS (Permitir Cadastro e Login)
                .requestMatchers( 
                    "/h2-console/**", 
                    "/css/**", "/js/**", "/images/**", 
                    "/treinadores/novo",      // Permite GET: Carregar o formulário
                    "/treinadores",           // Permite POST: Enviar/Salvar o formulário
                    "/login"                  // Permite acesso ao formulário de login
                    ).permitAll() 

                // 3. O RESTANTE (Index, Pokemons, Interesses, Solicitacoes) requer APENAS autenticação
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/", true) // Redireciona para o index após o login
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .permitAll()
            );

        // Configuração necessária para o H2 Console funcionar com Spring Security
        http.csrf(csrf -> csrf.disable());
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}