package com.reservemate.reserve_mate_backend.facility.dto.response;

import lombok.Getter;

@Getter
public class ResponseFacilitiesDto {

    private Long facilityId;
    private String facilityName;
    private String sportType;
    private String address;
    private Long courtId;
    private String courtName;
    private Integer fee;
    private String imageUrl;

    public ResponseFacilitiesDto(Long facilityId, String facilityName, String sportType,
        String address,
        Long courtId, String courtName, Integer fee, String imageUrl) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.sportType = sportType;
        this.address = address;
        this.courtId = courtId;
        this.courtName = courtName;
        this.fee = fee;
        this.imageUrl = imageUrl;
    }
}
