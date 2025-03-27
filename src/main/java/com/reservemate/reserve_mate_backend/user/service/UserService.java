package com.reservemate.reserve_mate_backend.user.service;

import com.reservemate.reserve_mate_backend.common.mail.service.MailService;
import com.reservemate.reserve_mate_backend.common.mail.util.RedisEmailAuthentication;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.domain.UserRole;
import com.reservemate.reserve_mate_backend.user.dto.request.RequestUserDto;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final RedisEmailAuthentication redisEmailAuthentication;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
        MailService mailService, RedisEmailAuthentication redisEmailAuthentication) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
        this.redisEmailAuthentication = redisEmailAuthentication;
    }

    public void registerUser(RequestUserDto requestUserDto) {
        // 이메일 중복체크
        boolean emailExists = userRepository.existsByEmail(requestUserDto.getEmail());

        if (emailExists) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        User user = User.builder()
            .name(requestUserDto.getName())
            .email(requestUserDto.getEmail())
            .password(passwordEncoder.encode(requestUserDto.getPassword())) // 암호화
            .phone(requestUserDto.getPhone())
            .profileImage(requestUserDto.getProfileImage())
            .role(UserRole.ROLE_USER) // 회원가입 하는 경우 유저로 셋팅
            .build();
        // save
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
        user.delete();
    }

    @Transactional
    public void updateUser(Long id, RequestUserDto requestUserDto) {
        User user = userRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));
        user.updateUser(
            requestUserDto.getName(),
            requestUserDto.getEmail(),
            passwordEncoder.encode(requestUserDto.getPassword()),
            requestUserDto.getPhone(),
            requestUserDto.getProfileImage());
    }

    public void sendResetPasswordEmail(String email) throws MessagingException, UnsupportedEncodingException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자가 없습니다."));

        mailService.sendResetPasswordEmail(user.getEmail());
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        //redis에 uuid 있는지 확인
        String email = redisEmailAuthentication.getEmailByResetPasswordToken(token);
        if (email == null) {
            throw new IllegalArgumentException();
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("해당 이메일의 사용자가 존재하지 않습니다."));

        user.updatePassword(passwordEncoder.encode(newPassword));
        redisEmailAuthentication.deleteResetPasswordToken(token);
    }
}
