package com.reservemate.reserve_mate_backend.facility.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PopularFacilityResponse {

    private Long facilityId;
    private String name;
    private String description;
    private String imageUrl;

}
