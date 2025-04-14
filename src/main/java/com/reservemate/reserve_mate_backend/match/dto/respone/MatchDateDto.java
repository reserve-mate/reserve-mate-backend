package com.reservemate.reserve_mate_backend.match.dto.respone;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.reservemate.reserve_mate_backend.common.util.Utils;

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
public class MatchDateDto {

    private LocalDate matchDate;
    private Long matchCnt;

    public static List<MatchDateDto> getMatchMonthDates(LocalDate matchDate, List<MatchDateDto> dateDtos) {
        List<MatchDateDto> matchDateDtos = new ArrayList<>();

        List<LocalDate> monthDates = Utils.getDateOfTwoWeeks(matchDate); // 한달 날짜를 담은 데이터

        for (LocalDate localDate : monthDates) {

            MatchDateDto matchDateDto = getMatchDateDto(localDate, dateDtos);

            matchDateDtos.add(matchDateDto);

        }

        return matchDateDtos;
    }

    private static MatchDateDto getMatchDateDto(LocalDate date, List<MatchDateDto> dateDtos) {

        MatchDateDto dateDto = dateDtos.stream()
            .filter(dto -> date.isEqual(dto.getMatchDate()))
            .findFirst()
            .orElse(null);

        Long matchCnt = (dateDto != null) ? dateDto.getMatchCnt() : 0L;

        MatchDateDto matchDateDto = MatchDateDto.builder()
            .matchDate(date)
            .matchCnt(matchCnt)
            .build();

        return matchDateDto;
    }

}
