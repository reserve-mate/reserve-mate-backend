package com.reservemate.reserve_mate_backend.common.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.auth.domain.RefreshToken;
import com.reservemate.reserve_mate_backend.common.auth.repository.RefreshRepository;
import com.reservemate.reserve_mate_backend.common.auth.service.CustomUserDetails;
import com.reservemate.reserve_mate_backend.user.domain.UserRole;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
        RefreshRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
        Authentication authentication) {

        //로그인 성공한 유저
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        log.info("====================================================");
        log.info("================Login success for user: {} ========", customUserDetails.getUsername());
        log.info("====================================================");

        String email = customUserDetails.getUsername();
        Long id = customUserDetails.getId();
        String role = authentication.getAuthorities().stream()
            .findFirst()
            .map(GrantedAuthority::getAuthority)
            .orElse(String.valueOf(UserRole.ROLE_USER));

        /*
        * 토큰발급
        * access : 10분
        * refresh : 24시간
        * */
        String access = jwtUtil.createJwt("access", id, email, role, 600000L);
        String refresh = jwtUtil.createJwt("refresh", id, email, role, 86400000L);

        //save refreshToken
        addRefreshToken(id, refresh, 86400000L);

        //응답
        response.setHeader("access", access);
        response.setHeader("Access-Control-Expose-Headers", "access");

        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());

    }

    private void addRefreshToken(Long id, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken refreshToken = RefreshToken.builder()
            .id(id)
            .refresh(refresh)
            .expiration(date.toString())
            .build();

        refreshRepository.save(refreshToken);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60); //24시간
        cookie.setHttpOnly(true);

        return cookie;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException failed) {
        log.info("====================================================");
        log.info("================Login falied: {} ========", failed.getMessage());
        log.info("====================================================");

        response.setStatus(401);
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
            //Authentication Manager 로 인증 수행
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("Authentication error", e);
        }

    }

}
