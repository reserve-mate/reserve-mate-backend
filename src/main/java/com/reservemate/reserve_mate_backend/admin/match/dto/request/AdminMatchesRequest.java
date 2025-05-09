package com.reservemate.reserve_mate_backend.admin.match.dto.request;

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
public class AdminMatchesRequest {  // 관리자 매치 목록 검색

    private String searchValue;     // 검색어
    private SportType sportType;    // 매치 종목
    private LocalDate startDate;    // 검색 시작 날짜
    private LocalDate endDate;      // 검색 마지막 날짜
    private int pageNumber;         // 페이징

}
