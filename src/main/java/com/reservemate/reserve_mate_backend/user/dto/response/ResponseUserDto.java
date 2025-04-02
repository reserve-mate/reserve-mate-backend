package com.reservemate.reserve_mate_backend.user.dto.response;

import com.reservemate.reserve_mate_backend.user.domain.User;
import lombok.Getter;

@Getter
public class ResponseUserDto {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String profileImage;
    private String role;

    public ResponseUserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.profileImage = user.getProfileImage();
        this.role = user.getRole().name();
    }
}
