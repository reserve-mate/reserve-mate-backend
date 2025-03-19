package com.reservemate.reserve_mate_backend.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestUserDto {

    private String name;
    private String email;
    private String password;
    private String phone;
}
