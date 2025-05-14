package com.reservemate.reserve_mate_backend.facility.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FacilityDto {

    private Long facilityId;

    private String facilityName;
    private String sportType;
    private String address;
    private long courtCount;
    private long reservationCount;

    public FacilityDto(Long facilityId, String facilityName, String sportType, String address,
        long courtCount, long reservationCount) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.sportType = sportType;
        this.address = address;
        this.courtCount = courtCount;
        this.reservationCount = reservationCount;
    }
}
