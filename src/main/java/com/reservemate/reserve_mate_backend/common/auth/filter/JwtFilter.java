package com.reservemate.reserve_mate_backend.common.auth.filter;

import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.auth.service.CustomUserDetails;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.domain.UserRole;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        //헤더에서 access token 꺼냄
        String accessToken = request.getHeader("access");

        //accessToken null 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        //accessToken 만료 여부 확인
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("accessToken expired");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //accessToken 확인
        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //accessToken 에서 email, role 값 가져오기
        String email = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        User user = User.builder()
            .email(email)
            .role(UserRole.valueOf(role))
            .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails
            .getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
