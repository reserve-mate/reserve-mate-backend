package com.reservemate.reserve_mate_backend.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestChangePasswordDto {

    private String currentPassword;
    private String newPassword;
}
