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

}
