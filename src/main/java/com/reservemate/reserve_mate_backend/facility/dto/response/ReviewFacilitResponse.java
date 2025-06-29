package com.reservemate.reserve_mate_backend.facility.dto.response;

import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ReviewFacilitResponse {

    private String facilityName;
    private SportType sportType;

    public static ReviewFacilitResponse toReviewFacilitResponse(Facility facility) {
        return ReviewFacilitResponse.builder()
            .facilityName(facility.getName())
            .sportType(facility.getSportType())
            .build();
    }

}
