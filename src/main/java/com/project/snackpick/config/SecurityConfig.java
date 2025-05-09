package com.project.snackpick.config;

import com.project.snackpick.handler.CustomCsrfTokenRequestHandler;
import com.project.snackpick.handler.LoginFailureHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, LoginFailureHandler authenticationFailureHandler) throws Exception {

        CsrfTokenRequestHandler requestHandler = new CustomCsrfTokenRequestHandler();

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(requestHandler));

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/member/**", "/product/**", "/review/**", "/", "/index.page").permitAll()
                        .requestMatchers("/assets/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/.well-known/**").permitAll()
                        .anyRequest().authenticated());

        http
                .formLogin((auth) -> auth.loginPage("/member/login.page")
                        .usernameParameter("id")
                        .defaultSuccessUrl("/", false)
                        .failureHandler(authenticationFailureHandler)
                        .loginProcessingUrl("/member/login").permitAll());

        http
                .logout(logout -> logout
                        .logoutUrl("/member/logout")
                        .logoutSuccessUrl("/"));

        http
                .sessionManagement((auth) -> auth
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true));

        http
                .sessionManagement((auth) -> auth
                        .sessionFixation().changeSessionId());

        return http.build();
    }

}
