package com.reservemate.reserve_mate_backend.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("빌더 패턴으로 모든 필드가 설정된 User 객체 생성")
    void builder_WithAllFields_ShouldCreateUserWithAllProperties() {
        // given
        String name = "사용자명";
        String email = "user@example.com";
        String password = "password";
        String phone = "010-1234-5678";
        String profileImage = "profile.jpg";
        UserRole role = UserRole.ROLE_USER;

        // when
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .phone(phone)
                .profileImage(profileImage)
                .role(role)
                .build();

        // then
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getPhone()).isEqualTo(phone);
        assertThat(user.getProfileImage()).isEqualTo(profileImage);
        assertThat(user.getRole()).isEqualTo(role);
    }

    @Test
    @DisplayName("빌더 패턴으로 필수 필드만 설정된 User 객체 생성")
    void builder_WithRequiredFieldsOnly_ShouldCreateUser() {
        // given
        String name = "사용자명";
        String email = "user@example.com";
        String password = "password";

        // when
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        // then
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getPhone()).isNull();
        assertThat(user.getProfileImage()).isNull();
        assertThat(user.getRole()).isEqualTo(UserRole.ROLE_USER); // 기본값이 ROLE_USER로 설정되어야 함
    }

    @Test
    @DisplayName("역할(role)이 null이면 기본값으로 ROLE_USER가 설정되어야 함")
    void builder_WithNullRole_ShouldSetDefaultRoleToUser() {
        // given
        String name = "사용자명";
        String email = "user@example.com";
        String password = "password";

        // when
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .role(null)
                .build();

        // then
        assertThat(user.getRole()).isEqualTo(UserRole.ROLE_USER);
    }

    @Test
    @DisplayName("updateProfile 메서드로 프로필 정보 업데이트")
    void updateProfile_ShouldUpdateProfileInformation() {
        // given
        User user = User.builder()
                .name("기존 이름")
                .email("user@example.com")
                .password("password")
                .phone("010-1111-1111")
                .profileImage("old-profile.jpg")
                .build();

        String newName = "새 이름";
        String newPhone = "010-2222-2222";
        String newProfileImage = "new-profile.jpg";

        // when
        user.updateProfile(newName, newPhone, newProfileImage);

        // then
        assertThat(user.getName()).isEqualTo(newName);
        assertThat(user.getPhone()).isEqualTo(newPhone);
        assertThat(user.getProfileImage()).isEqualTo(newProfileImage);
        assertThat(user.getEmail()).isEqualTo("user@example.com");
        assertThat(user.getPassword()).isEqualTo("password");
    }

    @Test
    @DisplayName("updateProfile 메서드에 null 값을 전달하면 기존 값이 유지되어야 함")
    void updateProfile_WithNullValues_ShouldKeepExistingValues() {
        // given
        String originalName = "기존 이름";
        String originalPhone = "010-1111-1111";
        String originalProfileImage = "old-profile.jpg";
        
        User user = User.builder()
                .name(originalName)
                .email("user@example.com")
                .password("password")
                .phone(originalPhone)
                .profileImage(originalProfileImage)
                .build();

        // when
        user.updateProfile(null, null, null);

        // then
        assertThat(user.getName()).isEqualTo(originalName);
        assertThat(user.getPhone()).isEqualTo(originalPhone);
        assertThat(user.getProfileImage()).isEqualTo(originalProfileImage);
    }

    @Test
    @DisplayName("updateProfile 메서드에 일부 null 값을 전달하면 해당 필드만 기존 값이 유지되어야 함")
    void updateProfile_WithSomeNullValues_ShouldUpdateOnlyNonNullValues() {
        // given
        String originalName = "기존 이름";
        String originalPhone = "010-1111-1111";
        String originalProfileImage = "old-profile.jpg";
        
        User user = User.builder()
                .name(originalName)
                .email("user@example.com")
                .password("password")
                .phone(originalPhone)
                .profileImage(originalProfileImage)
                .build();

        String newName = "새 이름";
        String newPhone = "010-2222-2222";

        // when
        user.updateProfile(newName, newPhone, null);

        // then
        assertThat(user.getName()).isEqualTo(newName);
        assertThat(user.getPhone()).isEqualTo(newPhone);
        assertThat(user.getProfileImage()).isEqualTo(originalProfileImage); // 변경되지 않아야 함
    }

    @Test
    @DisplayName("updatePassword 메서드로 비밀번호 업데이트")
    void updatePassword_ShouldUpdatePassword() {
        // given
        User user = User.builder()
                .name("사용자명")
                .email("user@example.com")
                .password("old-password")
                .build();
        
        String newPassword = "new-password";

        // when
        user.updatePassword(newPassword);

        // then
        assertThat(user.getPassword()).isEqualTo(newPassword);
    }

    @Test
    @DisplayName("ROLE_FACILITY_MANAGER 역할로 사용자 생성")
    void builder_WithFacilityManagerRole_ShouldCreateUserWithFacilityManagerRole() {
        // given
        String name = "시설 관리자";
        String email = "manager@example.com";
        String password = "password";
        UserRole role = UserRole.ROLE_FACILITY_MANAGER;

        // when
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .role(role)
                .build();

        // then
        assertThat(user.getRole()).isEqualTo(UserRole.ROLE_FACILITY_MANAGER);
    }

    @Test
    @DisplayName("ROLE_ADMIN 역할로 사용자 생성")
    void builder_WithAdminRole_ShouldCreateUserWithAdminRole() {
        // given
        String name = "관리자";
        String email = "admin@example.com";
        String password = "password";
        UserRole role = UserRole.ROLE_ADMIN;

        // when
        User user = User.builder()
                .name(name)
                .email(email)
                .password(password)
                .role(role)
                .build();

        // then
        assertThat(user.getRole()).isEqualTo(UserRole.ROLE_ADMIN);
    }
} 