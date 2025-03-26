package com.reservemate.reserve_mate_backend.common.mail.service;

import com.reservemate.reserve_mate_backend.common.mail.dto.MailDto;
import com.reservemate.reserve_mate_backend.common.mail.util.RedisEmailAuthentication;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final RedisEmailAuthentication redisEmailAuthentication;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.smtp.address}")
    private String address;
    @Value("${spring.mail.smtp.personal}")
    private String personal;

    public MailService(RedisEmailAuthentication redisEmailAuthentication, JavaMailSender mailSender) {
        this.redisEmailAuthentication = redisEmailAuthentication;
        this.mailSender = mailSender;
    }

    public void sendAuthCode(String email) throws MessagingException, UnsupportedEncodingException {
        //인증코드 생성
        String code = createAuthCode();

        //redis 에 인증코드 저장 유효기간: 5분
        redisEmailAuthentication.setEmailAuthenticationExpire(email, code, 5L);

        String text = "";
        text += "안녕하세요 ReserveMate 입니다.";
        text += "<br/>";
        text += "요청하신 인증코드 입니다.";
        text += "<br/>";
        text += "인증코드 : <b>" + code + "</b>";

        MailDto data = MailDto.builder()
            .email(email)
            .title("ReserveMate 인증코드 발송 메일입니다.")
            .text(text)
            .build();

        this.sendMail(data);
    }

    private void sendMail(MailDto data) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = createMessage(data.getEmail(), data.getTitle(), data.getText());
        mailSender.send(message);

    }

    private MimeMessage createMessage(String receiver, String title,
        String text) throws UnsupportedEncodingException, MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress(address, personal));
        message.setRecipients(RecipientType.TO, receiver);
        message.setSubject(title);
        message.setText(text, "UTF-8", "html");

        return message;
    }

    private String createAuthCode() {
        //6자리 난수
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    public void checkAuthCode(String email, String authNum) {
        String code = redisEmailAuthentication.getEmailAuthentication(email);

        //redis 에서 해당 이메일 존재하는지 확인
        if (code == null) {
            throw new IllegalArgumentException("등록되지 않은 이메일입니다.");
        }
        //입력한 인증코드와 발송된 인증코드 값 비교
        if (!code.equals(authNum)) {
            throw new IllegalArgumentException("이메일 인증코드가 일치하지 않습니다.");
        }

        //이메일 인증 완료처리
        redisEmailAuthentication.setEmailAuthenticationComplete(email);
    }
}
