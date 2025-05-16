package com.reservemate.reserve_mate_backend.admin.match.dto.response;

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
public class AdminMatchesResponse { // 관리자 매치 목록

    private Long matchId;
    private String matchName;
    private SportType sportType;
    private String facilityName;
    private LocalDate matchDate;
    private int teamCapacity;
    private Long playerCnt;
    private int matchTime;
    private int endTime;
    private MatchStatus matchStatus;

}
