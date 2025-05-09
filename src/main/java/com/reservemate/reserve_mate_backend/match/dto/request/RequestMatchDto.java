package com.reservemate.reserve_mate_backend.match.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RequestMatchDto {

    private String orderId; // 주문 번호

    @NotNull
    private Long matchId;

    @NotNull
    private Integer amount; // 매치 가격

}
