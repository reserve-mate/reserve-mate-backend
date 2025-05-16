package com.reservemate.reserve_mate_backend.match.dto.request;

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
public class CancelMatchRequest {

    private Long matchId;
    private String orderId;
    private String cancelReason;

}
