package com.reservemate.reserve_mate_backend.common.auth.service;

import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.auth.domain.RefreshToken;
import com.reservemate.reserve_mate_backend.common.auth.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public AuthService(JwtUtil jwtUtil, RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        //refreshToken 가져오기
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }
        log.info("==========================================");
        log.info("============Reissue======================");
        log.info("=============refreshToken : " + refresh);
        log.info("========================================");
        //null check
        if (refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //check expired
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        //refresh Token 확인
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }
        //redis 저장 확인
        if (!refreshRepository.findById(refresh).isPresent()) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        Long id = jwtUtil.getId(refresh);
        String email = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        //새로운 JWT 발급
        String newAccess = jwtUtil.createJwt("access", id, email, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", id, email, role, 86400000L);

        //기존 refreshToken 삭제, 새로운 refreshToken 저장
        refreshRepository.deleteById(refresh);
        addRefreshToken(id, newRefresh, 86400000L);

        log.info("==========================================");
        log.info("======NewAccessToken: " + newAccess);
        log.info("======NewRefreshToken : " + newRefresh);
        log.info("========================================");
        //응답
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);

        return cookie;
    }

    private void addRefreshToken(Long id, String newRefresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);
        RefreshToken refresh = RefreshToken.builder()
            .id(id)
            .refresh(newRefresh)
            .expiration(date.toString())
            .build();

        refreshRepository.save(refresh);
    }
}
