package com.reservemate.reserve_mate_backend.match.dto.request;

import java.time.LocalDate;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
public class CreateMatchDto {

    @NotEmpty(message = "매치명은 필수 입력 항목입니다.")
    private String matchName;

    @NotNull(message = "코트 번호는 필수 입력 항목입니다.")
    private Long courtId;

    @NotNull(message = "회원 정보는 필수 입력 항목입니다.")
    private Long managerId;

    @NotNull(message = "최대 참가 인원은 필수 입력 항목입니다.")
    private Integer teamCapacity;

    @NotNull(message = "매치 날짜는 필수 입력 항목입니다.")
    private LocalDate matchDate;

    @NotNull(message = "매치 시간은 필수 입력 항목입니다.")
    private Integer matchTime;

    @NotNull(message = "매치 종료 시간은 필수 입력 항목입니다.")
    private Integer matchEndTime;

    @NotNull(message = "매치 가격은 필수 입력 항목입니다.")
    private Integer matchPrice;

    private String description;

    public Match toEntity(CreateMatchDto createMatchDto, Court court, FacilityManager facilityManager) {
        return Match.builder()
            .matchName(createMatchDto.getMatchName())
            .matchStatus(MatchStatus.APPLICABLE)
            .teamCapacity(createMatchDto.getTeamCapacity())
            .matchDate(createMatchDto.getMatchDate())
            .matchTime(createMatchDto.getMatchTime())
            .endTime(createMatchDto.getMatchEndTime())
            .matchPrice(createMatchDto.getMatchPrice())
            .description(createMatchDto.getDescription())
            .court(court)
            .facilityManager(facilityManager)
            .build();
    }

}
