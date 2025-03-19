package com.reservemate.reserve_mate_backend.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.domain.UserRole;
import com.reservemate.reserve_mate_backend.user.dto.request.RequestUserDto;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private RequestUserDto validUserDto;
    private String encodedPassword = "encodedPassword";

    @BeforeEach
    void setUp() {
        validUserDto = new RequestUserDto("테스트유저", "test@example.com", "password123", "010-1234-5678", null);
    }

    @Test
    @DisplayName("회원가입 성공 케이스 - 모든 필드가 올바르게 설정되어야 함")
    void registerUser_Success() {
        // given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn(encodedPassword);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // when
        userService.registerUser(validUserDto);

        // then
        verify(userRepository, times(1)).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getName()).isEqualTo(validUserDto.getName());
        assertThat(savedUser.getEmail()).isEqualTo(validUserDto.getEmail());
        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(savedUser.getPhone()).isEqualTo(validUserDto.getPhone());
        assertThat(savedUser.getRole()).isEqualTo(UserRole.ROLE_USER);
    }

    @Test
    @DisplayName("중복된 이메일로 회원가입 시도 시 예외가 발생해야 함")
    void registerUser_WithDuplicateEmail_ThrowsException() {
        // given
        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.registerUser(validUserDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 사용중인 이메일입니다.");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("비밀번호는 암호화되어야 함")
    void registerUser_ShouldEncodePassword() {
        // given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(validUserDto.getPassword())).willReturn(encodedPassword);

        // when
        userService.registerUser(validUserDto);

        // then
        verify(passwordEncoder, times(1)).encode(validUserDto.getPassword());
    }

    @Test
    @DisplayName("사용자 정보가 리포지토리에 저장되어야 함")
    void registerUser_ShouldSaveToRepository() {
        // given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn(encodedPassword);

        // when
        userService.registerUser(validUserDto);

        // then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 역할이 기본값으로 ROLE_USER로 설정되어야 함")
    void registerUser_ShouldSetDefaultRoleToUser() {
        // given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn(encodedPassword);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // when
        userService.registerUser(validUserDto);

        // then
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getRole()).isEqualTo(UserRole.ROLE_USER);
    }

    @Test
    @DisplayName("이메일 중복 확인을 위해 리포지토리가 호출되어야 함")
    void registerUser_ShouldCheckEmailDuplication() {
        // given
        String email = "test@example.com";
        given(userRepository.existsByEmail(email)).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn(encodedPassword);

        // when
        userService.registerUser(validUserDto);

        // then
        verify(userRepository, times(1)).existsByEmail(email);
    }

    @Test
    @DisplayName("유효한 DTO의 모든 필드가 User 엔티티에 정확히 매핑되어야 함")
    void registerUser_ShouldMapAllFieldsCorrectly() {
        // given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn(encodedPassword);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        RequestUserDto userDto = new RequestUserDto("홍길동", "hong@example.com", "securePassword", "010-9876-5432", "profile.jpg");

        // when
        userService.registerUser(userDto);

        // then
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getName()).isEqualTo("홍길동");
        assertThat(savedUser.getEmail()).isEqualTo("hong@example.com");
        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(savedUser.getPhone()).isEqualTo("010-9876-5432");
        assertThat(savedUser.getProfileImage()).isEqualTo("profile.jpg");
    }

    @Test
    @DisplayName("사용자 프로필 이미지가 null이어도 회원가입에 성공해야 함")
    void registerUser_WithNullProfileImage_ShouldSucceed() {
        // given
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn(encodedPassword);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // when
        userService.registerUser(validUserDto);

        // then
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getProfileImage()).isNull();
    }
}
