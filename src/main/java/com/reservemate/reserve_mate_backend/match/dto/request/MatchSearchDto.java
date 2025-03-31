package com.reservemate.reserve_mate_backend.match.dto.request;

import java.time.LocalDate;

import com.reservemate.reserve_mate_backend.facility.domain.SportType;

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
public class MatchSearchDto {

    private String search;
    private SportType sportType;
    private LocalDate matchDate;

}
