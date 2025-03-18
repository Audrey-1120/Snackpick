package com.project.snackpick.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

import java.util.function.Supplier;

public final class CustomCsrfTokenRequestHandler implements CsrfTokenRequestHandler  {

    // multipart/form-data 요청에서 CSRF 토큰을 정상적으로 읽어오기 위한 핸들러입니다..

    private final XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
        this.delegate.handle(request, response, csrfToken);
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {

        // 요청의 헤더에서 CSRF 토큰을 먼저 찾고, 없으면 기본 방식 사용
        String token = request.getHeader(csrfToken.getHeaderName());
        if(token == null) {
            token = this.delegate.resolveCsrfTokenValue(request, csrfToken);
        }
        return token;
    }
}
