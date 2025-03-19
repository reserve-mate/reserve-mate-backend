package com.reservemate.reserve_mate_backend.user.controller;

import com.reservemate.reserve_mate_backend.user.dto.request.RequestUserDto;
import com.reservemate.reserve_mate_backend.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 회원등록
    @PostMapping("/register")
    public void registerUser(@RequestBody RequestUserDto requestUserDto) {
        userService.registerUser(requestUserDto);
    }

    // 유저 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // 유저 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, RequestUserDto requestUserDto) {
        userService.updateUser(id, requestUserDto);
        return ResponseEntity.ok().build();
    }
}
