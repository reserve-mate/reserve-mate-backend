package com.reservemate.reserve_mate_backend.common.mail.controller;

import com.reservemate.reserve_mate_backend.common.mail.dto.MailCheckDto;
import com.reservemate.reserve_mate_backend.common.mail.dto.MailRequestDto;
import com.reservemate.reserve_mate_backend.common.mail.service.MailService;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    //인증코드 메일 전송 요청
    @PostMapping("/send/authCode")
    public ResponseEntity<String> sendAuthCode(
        @RequestBody MailRequestDto requestDto) throws MessagingException, UnsupportedEncodingException {
        System.out.println("이메일 인증 메일주소:" + requestDto.getEmail());
        mailService.sendAuthCode(requestDto.getEmail());
        return ResponseEntity.ok("인증코드가 이메일로 전송되었습니다.");
    }

    @PostMapping("check/authCode")
    public ResponseEntity<String> checkAuthCode(@RequestBody MailCheckDto mailCheckDto) {
        try {
            mailService.checkAuthCode(mailCheckDto.getEmail(), mailCheckDto.getAuthNum());
            return ResponseEntity.ok("인증 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
