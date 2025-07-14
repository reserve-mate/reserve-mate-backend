package com.reservemate.reserve_mate_backend.facility.dto.response;

import lombok.Getter;

@Getter
public class ResponseReviewFacilityDto {

    private Long id;
    private Integer rating;
    private String title;
    private String content;
    private String userName;
    private Long reservationId;

    public ResponseReviewFacilityDto(Long id, Integer rating, String title, String content) {
        this.id = id;
        this.rating = rating;
        this.title = title;
        this.content = content;
    }

}
