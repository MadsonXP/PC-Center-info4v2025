package br.edu.ifrn.PcCenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; 
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService; 
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Injeção da sua implementação de UserDetailsService (TreinadorDetailsService)
    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Rotas Públicas
                .requestMatchers( 
                    "/h2-console/**", 
                    "/css/**", "/js/**", "/images/**", 
                    "/treinadores/novo",      
                    "/treinadores",           
                    "/login"                  
                    ).permitAll() 
                
                // Rotas Restritas
                .requestMatchers("/treinadores", "/treinadores/**").hasRole("ADMIN")
                
                // O RESTANTE requer autenticação
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/", true)
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .permitAll()
            )
            // CRÍTICO: Configura o provedor de autenticação customizado
            .authenticationProvider(authenticationProvider()); 

        http.csrf(csrf -> csrf.disable());
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    // Bean que define o codificador de senhas (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // CRÍTICO: Bean que configura o provedor de autenticação para usar sua classe TreinadorDetailsService e o BCrypt
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // Usa sua implementação
        authProvider.setPasswordEncoder(passwordEncoder());      // Usa o BCrypt
        return authProvider;
    }
}