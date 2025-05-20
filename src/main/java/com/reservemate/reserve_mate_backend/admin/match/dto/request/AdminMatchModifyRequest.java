package com.reservemate.reserve_mate_backend.admin.match.dto.request;

import java.time.LocalDate;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;

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
public class AdminMatchModifyRequest {

    private String matchTitle;      // 수정 가능 데이터
    private LocalDate matchDate;
    private Integer matchTime;
    private Integer endTime;
    private Long facilityCourtId;   // 수정 가능 데이터
    private Long managerId;         // 수정 가능 데이터
    private Integer teamCapacity;   // 수정 가능 데이터
    private String description;     // 수정 가능 데이터

    // 팀원을 현재 참가 인원보다 적게 설정하려는 경우
    public void isTeamCapacityOver(int playerCnt) {
        if (this.teamCapacity < playerCnt) {
            throw new ApiException(ErrorCode.MAX_TEAM_SIZE_CONFLICT);
        }
    }

}
