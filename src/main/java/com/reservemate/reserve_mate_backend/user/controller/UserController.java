package com.reservemate.reserve_mate_backend.user.controller;

import com.reservemate.reserve_mate_backend.common.mail.dto.RequestMailDto;
import com.reservemate.reserve_mate_backend.common.mail.dto.RequestResetPasswordDto;
import com.reservemate.reserve_mate_backend.common.sms.dto.RequestFindEmailDto;
import com.reservemate.reserve_mate_backend.user.dto.request.RequestUserDto;
import com.reservemate.reserve_mate_backend.user.dto.response.ResponseUserDto;
import com.reservemate.reserve_mate_backend.user.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<ResponseUserDto> registerUser(@RequestBody RequestUserDto requestUserDto) {
        return userService.registerUser(requestUserDto);
    }

    // 유저 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // 유저 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable Long id, @RequestBody RequestUserDto requestUserDto) {
        userService.updateUser(id, requestUserDto);
        return ResponseEntity.ok().build();
    }

    //비밀번호 찾기
    @PostMapping("/find/password")
    public ResponseEntity<String> findPassword(@RequestBody RequestMailDto requestDto) {
        try {
            userService.sendResetPasswordEmail(requestDto.getEmail());
            return ResponseEntity.ok("비밀번호 재설정 이메일이 전송되었습니다.");
        } catch (MessagingException | UnsupportedEncodingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 재설정 이메일 전송이 실패했습니다. 다시 시도해주세요.");
        }
    }

    //비밀번호 재설정
    @PostMapping("/reset/password")
    public ResponseEntity<String> resetPassword(@RequestBody RequestResetPasswordDto requestDto) {
        userService.resetPassword(requestDto.getToken(), requestDto.getNewPassword());
        return ResponseEntity.ok("비밀번호가 변경되었습니다.");
    }

    @GetMapping("/find/password/reset")
    public ResponseEntity<String> resetPasswordPage(@RequestParam("token") String token) {
        return ResponseEntity.ok("비밀번호 재설정페이지로 이동했습니다. token :" + token);
    }

    //이메일 찾기
    @PostMapping("/find/email")
    public ResponseEntity<String> findEmail(@RequestBody RequestFindEmailDto findEmailRequestDto) {
        userService.sendAuthCodeForFindEmail(findEmailRequestDto);
        return ResponseEntity.ok("인증번호가 등록하신 휴대번호로 전송되었습니다.");
    }

    @PostMapping("/find/email/verify")
    public ResponseEntity<String> verifyEmailAuthCode(@RequestBody RequestFindEmailDto findEmailDto) {
        String email = userService.verifyAuthCodeAndReturnEmail(findEmailDto.getAuthCode(), findEmailDto.getPhone());
        return ResponseEntity.ok("가입하신 이메일은" + email + "입니다.");
    }

    //프로필 정보 페이지
    @GetMapping("/me/profile")
    public ResponseEntity<ResponseUserDto> profilePage(HttpServletRequest request, HttpServletResponse response) {
        return userService.profilePage(request, response);
    }

    //프로필 정보 수정
    @PutMapping("/me/update/profile/{id}")
    public ResponseEntity<String> updateProfile(@PathVariable Long id, @RequestBody RequestUserDto request) {
        return userService.updateProfile(id, request);
    }

}
