package com.reservemate.reserve_mate_backend.match.dto.respone;

import java.time.LocalDate;

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
    private String matchName;
    private MatchStatus matchStatus;
    private String facilityName;
    private String fullAddress;
    private LocalDate matchDate;
    private Integer matchTime;
    private Integer matchEndTime;
    private SportType sportType;
    private int matchPrice;
    private int teamCapacity;
    private Long playerCnt;

}
