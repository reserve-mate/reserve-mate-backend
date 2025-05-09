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
}
