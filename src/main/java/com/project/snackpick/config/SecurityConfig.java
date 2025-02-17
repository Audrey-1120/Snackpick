package com.project.snackpick.config;

import com.project.snackpick.handler.LoginFailureHandler;
import jakarta.servlet.http.HttpSession;
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
    public SecurityFilterChain filterChain(HttpSecurity http, LoginFailureHandler authenticationFailureHandler) throws Exception {

        // 경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth // 람다식 형태 활용
                        .requestMatchers("/member/**", "/", "/index.page").permitAll() // 로그인, 회원가입, 메인(루트)경로에 대해서는 모든 권한 허용(permitAll())
                        .requestMatchers("/assets/**", "/css/**", "/js/**", "/images/**").permitAll() // 정적 파일 허용
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/admin").hasRole("ADMIN") // admin 경로는 ADMIN이라는 권한만 가진 사용자만 접근 가능
                        .anyRequest().authenticated()); // 그외 나머지 요청에서는 로그인한 사용자만 접근 가능

        http
                .formLogin((auth) -> auth.loginPage("/member/login.page")
                        .usernameParameter("id")
                        .defaultSuccessUrl("/", true)
                        .failureHandler(authenticationFailureHandler)
                        .loginProcessingUrl("/member/login").permitAll());

        http
                .logout(logout -> logout
                        .logoutUrl("/member/logout")
                        .logoutSuccessUrl("/")
                        .addLogoutHandler((request, response, authentication) -> {
                            HttpSession session = request.getSession();
                            session.invalidate();
                        })
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.sendRedirect("/"))
                        .deleteCookies("JSESSIONID", "access_token"));

        // 개발환경에서는 토큰을 보내지 않으면 로그인이 진행이 안되므로 개발환경에서만 csrf 환경을 disabled한다.
        http
                .csrf((auth) -> auth.disable());


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
