package com.reservemate.reserve_mate_backend.common.mail.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MailDto {

    private String email;
    private String title;
    private String text;
}
