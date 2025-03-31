package com.reservemate.reserve_mate_backend.common.mail.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestResetPasswordDto {

    private String token;
    private String newPassword;
}
