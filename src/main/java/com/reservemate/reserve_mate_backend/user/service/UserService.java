package com.reservemate.reserve_mate_backend.user.service;

import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.domain.UserRole;
import com.reservemate.reserve_mate_backend.user.dto.request.RequestUserDto;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
}
