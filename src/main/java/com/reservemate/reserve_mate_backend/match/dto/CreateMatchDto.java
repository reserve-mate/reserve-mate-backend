package com.reservemate.reserve_mate_backend.match.dto;

import java.time.LocalDate;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @NotNull(message = "user_id가 비어있습니다. 유효한 값을 입력해주세요.")
    private Long userId;

    private MatchStatus matchStatus;

    @Min(10)
    @Max(18)
    private int teamCapacity;

    @NotNull(message = "매치 날짜는 필수 입력 항목입니다.")
    private LocalDate matchDate;

    @Min(6)
    @Max(22)
    private int matchTime;

    @NotNull(message = "매치 가격은 필수 입력 항목입니다.")
    private Integer matchPrice;

    @NotNull(message = "코트 번호는 필수 입력 항목입니다.")
    private Long courtId;

    public Match toEntity(CreateMatchDto createMatchDto, Court court, String userName) {
        return Match.builder()
            .manager(userName)
            .matchStatus(MatchStatus.APPLICABLE)
            .teamCapacity(createMatchDto.getTeamCapacity())
            .matchDate(createMatchDto.getMatchDate())
            .matchTime(createMatchDto.getMatchTime())
            .matchPrice(createMatchDto.getMatchPrice())
            .court(court)
            .build();
    }

}
