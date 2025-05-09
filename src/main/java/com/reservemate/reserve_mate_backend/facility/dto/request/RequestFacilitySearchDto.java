package com.reservemate.reserve_mate_backend.facility.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestFacilitySearchDto {

    private String keyword;

    private Long lastId;
    private int size = 7;

    @Builder
    public RequestFacilitySearchDto(String keyword, Long lastId, int size) {
        this.keyword = keyword;
        this.lastId = lastId;
        this.size = size;
    }
}
