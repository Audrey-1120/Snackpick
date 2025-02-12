package com.project.snackpick.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Security를 위한 Config 클래스입니다.
public class SecurityConfig {

    // 비밀번호 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth // 람다식 형태 활용
                        .requestMatchers("/member/**", "/", "/index.page").permitAll() // 로그인, 회원가입, 메인(루트)경로에 대해서는 모든 권한 허용(permitAll())
                        .requestMatchers("/assets/**", "/css/**", "/js/**", "/images/**").permitAll() // 정적 파일 허용
                        .requestMatchers("/admin").hasRole("ADMIN") // admin 경로는 ADMIN이라는 권한만 가진 사용자만 접근 가능
                        .anyRequest().authenticated()); // 그외 나머지 요청에서는 로그인한 사용자만 접근 가능

        http
                .formLogin((auth) -> auth.loginPage("/member/login.page")
                        .usernameParameter("id")
                        .defaultSuccessUrl("/", true)
                        .loginProcessingUrl("/member/login").permitAll());

        // 개발환경에서는 토큰을 보내지 않으면 로그인이 진행이 안되므로 개발환경에서만 csrf 환경을 disabled한다.
        http
                .csrf((auth) -> auth.disable());

        return http.build();
    }

}
