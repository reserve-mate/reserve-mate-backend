package com.reservemate.reserve_mate_backend.common.auth.controller;

import com.reservemate.reserve_mate_backend.user.dto.request.RequestUserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody RequestUserDto request) {
        return ResponseEntity.ok("로그인 성공");
    }
}
