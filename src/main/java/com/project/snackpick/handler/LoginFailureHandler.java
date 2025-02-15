package com.project.snackpick.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@Component(value = "authenticationFailureHandler")
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        exception.printStackTrace();
        String errorMessage = URLEncoder.encode(getExceptionMessage(exception), "UTF-8");
        setDefaultFailureUrl("/member/loginFail?error=true&exception=" + errorMessage);
        super.onAuthenticationFailure(request, response, exception);
    }

    private String getExceptionMessage(AuthenticationException exception) {

        if(exception instanceof BadCredentialsException) {
            return "아이디 혹은 비밀번호를 다시 확인해주세요.";
        } else if(exception instanceof UsernameNotFoundException) {
            return "존재하지 않는 아이디입니다.";
        } else {
            return "알 수 없는 에러입니다.";
        }
    }
}
