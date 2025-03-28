package com.reservemate.reserve_mate_backend.match.dto.respone;

import com.reservemate.reserve_mate_backend.facility.domain.SportType;
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
public class MatchesDto {

    private Long matchId;
    private String courtName;
    private MatchStatus matchStatus;
    private String fullAddress;
    private String matchDate;
    private String matchTime;
    private SportType sportType;
    private int matchPrice;
    private int teamCapacity;

}
