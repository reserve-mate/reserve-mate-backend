package com.reservemate.reserve_mate_backend.admin.match.dto.request;

import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;

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
public class MatchStatusUpdateRequest {

    private MatchStatus matchStatus;

}
