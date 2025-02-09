package com.project.snackpick.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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

        // csrf disabled 설정
        // 세션방식에서는 세션이 항상 고정되기 때문에 csrf공격 방지가 필요함.
        // JWT는 세션을 stateless방식으로 관리하므로 csrf 공격에 그렇게 방어할 필요는 없음.

        http
                .csrf((auth) -> auth.disable());

        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        // 경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth // 람다식 형태 활용
                        .requestMatchers("/member/**", "/", "/index.page").permitAll() // 로그인, 회원가입, 메인(루트)경로에 대해서는 모든 권한 허용(permitAll())
                        .requestMatchers("/assets/**", "/css/**", "/js/**", "/images/**").permitAll() // 정적 파일 허용
                        .requestMatchers("/admin").hasRole("ADMIN") // admin 경로는 ADMIN이라는 권한만 가진 사용자만 접근 가능
                        .anyRequest().authenticated()); // 그외 나머지 요청에서는 로그인한 사용자만 접근 가능

        // 세션 설정(제일 중요한 부분!)
        // JWT에서는 세션을 항상 stateless형태로 관리함.
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

}
