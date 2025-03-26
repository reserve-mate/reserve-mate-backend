package com.reservemate.reserve_mate_backend.common.mail.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MailCheckDto {

    private String email;
    private String authNum;
}
