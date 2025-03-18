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

        // 커스텀 CSRF 토큰 핸들러 사용
        CsrfTokenRequestHandler requestHandler = new CustomCsrfTokenRequestHandler();

        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // CSRF 토큰 쿠키에 저장
                        .csrfTokenRequestHandler(requestHandler)); // 커스텀 핸들러 적용

        // 경로 인가
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/member/**", "/product/**", "/review/**", "/", "/index.page").permitAll()
                        .requestMatchers("/assets/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated());

        // 로그인
        http
                .formLogin((auth) -> auth.loginPage("/member/login.page")
                        .usernameParameter("id")
                        .defaultSuccessUrl("/", false)
                        .failureHandler(authenticationFailureHandler)
                        .loginProcessingUrl("/member/login").permitAll());

        // 로그아웃
        http
                .logout(logout -> logout
                        .logoutUrl("/member/logout")
                        .logoutSuccessUrl("/"));

        // 세션
        http
                .sessionManagement((auth) -> auth
                .maximumSessions(1) // 1개의 아이디에서 최대 몇개까지 중복 로그인 허용할 수 있는가?
                .maxSessionsPreventsLogin(true)); // 다중 로그인 개수를 초과하였을 경우 -> true(새로운 로그인 차단) / false(기존 세션 하나 삭제)

        // 세션 고정 보호
        http
                .sessionManagement((auth) -> auth
                        .sessionFixation().changeSessionId());

        return http.build();
    }

}
