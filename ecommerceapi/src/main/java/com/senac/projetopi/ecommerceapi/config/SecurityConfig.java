package com.senac.projetopi.ecommerceapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1) DESABILITAR CSRF para endpoints que serão chamados pelo JS sem token:
                .csrf(csrf -> csrf
                        // Ignorar CSRF para login, cliente, pedidos etc.
                        .ignoringRequestMatchers(
                                "/api/auth/**",
                                "/api/cliente/**",
                                "/api/cliente/auth/**",
                                "/api/pedidos/**"     // <– adicionamos /api/pedidos para ignorar CSRF
                        )
                        // Opcional: guardar o token de CSRF em cookie se quiser usar Thymeleaf,
                        // mas para nosso POST via JS, ignoramos o CSRF acima.
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                // 2) Configurar autorização por URL:
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/cliente/auth/**").permitAll()
                        .requestMatchers("/loja/**").permitAll()
                        .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()

                        // Permitir que o cliente autenticado finalize pedidos:
                        .requestMatchers("/api/pedidos/finalizar").authenticated()

                        // A própria API de cliente (ex.: listar perfil) exige autenticação:
                        .requestMatchers("/api/cliente/**").authenticated()
                        .requestMatchers("/loja/perfil/**").authenticated()

                        // Rotas administrativas – exigem ROLE_ADMIN etc.
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/usuarios").hasRole("ADMIN")
                        .requestMatchers("/api/produtos/**").hasAnyRole("ADMIN", "ESTOQUISTA")
                        .requestMatchers("/produtos").hasAnyRole("ADMIN", "ESTOQUISTA")

                        // Qualquer outra rota, precisa estar autenticado.
                        .anyRequest().authenticated()
                )
                // 3) Configurar formulário de login (continua igual)
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("senha")
                        .loginProcessingUrl("/api/auth/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                // 4) Configurar logout (continua igual)
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                // 5) Gerenciamento de sessão
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .expiredUrl("/login?expired=true")
                );

        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }
}
