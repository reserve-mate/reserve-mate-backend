package com.reservemate.reserve_mate_backend.match.dto.request;

import java.time.LocalDate;

import com.reservemate.reserve_mate_backend.facility.domain.SportType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class MatchSearchDto {

    private int pageNumber;

    private String searchValue;
    private SportType sportType;

    private LocalDate matchDate;

    public void initSportType() {
        if (this.sportType == SportType.ALL) {
            this.sportType = null;
        }
    }

    public void initMatchDateIfNull() {
        if (this.matchDate == null) {
            this.matchDate = LocalDate.now();
        }
    }

}
