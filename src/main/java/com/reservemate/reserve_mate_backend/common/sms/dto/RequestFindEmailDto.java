package com.reservemate.reserve_mate_backend.common.sms.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestFindEmailDto {

    private String phone;
    private String authCode;
}
