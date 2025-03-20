package com.reservemate.reserve_mate_backend.common.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public LoginFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
        HttpServletResponse response) throws AuthenticationException {
        try {
            //JSON DATA
            Map<String, String> credentials = new ObjectMapper().readValue(request.getInputStream(), Map.class);
            String email = credentials.get("email");
            String password = credentials.get("password");
            //인증토큰 생성
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);
            //Authentication Manaver 로 인증 수행
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("Authentication error", e);
        }

    }

}
