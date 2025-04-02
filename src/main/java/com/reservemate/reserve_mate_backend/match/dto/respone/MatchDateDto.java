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

        List<LocalDate> monthDates = Utils.getDateOfMonth(matchDate);

        for (LocalDate localDate : monthDates) {
            MatchDateDto dateDto = null;

            for (MatchDateDto matchDateDto : dateDtos) {
                dateDto = MatchDateDto.getMatchDateDto(localDate, matchDateDto);
            }

            matchDateDtos.add(dateDto);
        }

        return matchDateDtos;
    }

    private static MatchDateDto getMatchDateDto(LocalDate date, MatchDateDto matchDate) {

        Long matchCnt = (date.isEqual(matchDate.getMatchDate())) ? matchDate.getMatchCnt() : 0L;

        MatchDateDto dateDto = MatchDateDto.builder()
            .matchDate(date)
            .matchCnt(matchCnt)
            .build();

        return dateDto;
    }

}
