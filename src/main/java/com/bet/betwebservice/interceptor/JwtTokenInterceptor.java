package com.bet.betwebservice.interceptor;

import com.bet.betwebservice.authentication.JwtTokenWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtTokenInterceptor implements HandlerInterceptor {

    private JwtTokenWrapper jwtTokenWrapper;

    @Autowired
    private JwtDecoder jwtDecoder;

    public JwtTokenInterceptor(JwtTokenWrapper jwtTokenWrapper) {
        this.jwtTokenWrapper = jwtTokenWrapper;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
        ) throws Exception {
        System.out.println(String.format("[preHandle] [%s] [%s]", request.getRequestURI(), request.getHeader("Authorization")));
        final String authorizationHeaderValue = request.getHeader("Authorization");
        if (authorizationHeaderValue != null && authorizationHeaderValue.startsWith("Bearer")) {
            String jwtToken = authorizationHeaderValue.substring(7, authorizationHeaderValue.length());
            this.jwtTokenWrapper.setJwtToken(jwtToken);
            String jwtTokenSubject = jwtDecoder.decode(jwtToken).getClaims().get("sub").toString();
            this.jwtTokenWrapper.setJwtTokenSubject(jwtTokenSubject);
        }
        return true;
    }
}