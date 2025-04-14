package com.reservemate.reserve_mate_backend.common.exception;

import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class TossErrorDto {

    private String tossErrorCode;
    private String tossErrorMsg;

    public static ResponseEntity<TossErrorDto> toTossResponseEntity(TossApiException ex) {
        TossErrorDto tossErrorDto = TossErrorDto.builder()
            .tossErrorCode(ex.getTossErrorCode())
            .tossErrorMsg(ex.getTossErrorMsg())
            .build();
        return ResponseEntity.ok(tossErrorDto);
    }
}
