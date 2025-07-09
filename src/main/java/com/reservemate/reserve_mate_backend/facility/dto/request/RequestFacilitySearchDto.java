package com.reservemate.reserve_mate_backend.facility.dto.request;

import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestFacilitySearchDto {

    private String keyword;
    private Long lastId;
    private int size = 7;
    private SportType sportType;
    private Integer minPrice;
    private Integer maxPrice;

    @Builder
    public RequestFacilitySearchDto(String keyword, Long lastId, int size, SportType sportType, Integer minPrice,
        Integer maxPrice) {
        this.keyword = keyword;
        this.lastId = lastId;
        this.size = size;
        this.sportType = sportType;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

}
