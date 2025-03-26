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
public class ModifyMatchDto {

    private Integer teamCapacity;
    private String description;

    public void isOverTeamCapacity(int playerCnt) { // 준비된 인원 수 초과 검사
        if (playerCnt > this.teamCapacity) {
            throw new IllegalArgumentException("준비된 인원 수를 초과하는 값을 입력해 주세요.");
        }
    }

}
