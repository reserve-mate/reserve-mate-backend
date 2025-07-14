package com.reservemate.reserve_mate_backend.facility.dto.response;

import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseFacilityDetailDto {

    private Long facilityId;

    private String facilityName;
    private SportType sportType;
    private String address;
    private String description;

    private List<ResponseOperatingHourDto> hours;

    private List<ResponseCourtDto> courts;

    private List<ResponseReviewFacilityDto> reviews;

    private String managerPhoneNumber;

    private String imageUrl;

    private double rating;

    @Builder
    public ResponseFacilityDetailDto(Long facilityId, String facilityName, SportType sportType,
        String address, String description, List<ResponseOperatingHourDto> hours, List<ResponseCourtDto> courts,
        List<ResponseReviewFacilityDto> reviews,
        String managerPhoneNumber, String imageUrl, double rating) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.sportType = sportType;
        this.address = address;
        this.description = description;
        this.hours = hours;
        this.courts = courts;
        this.reviews = reviews;
        this.managerPhoneNumber = managerPhoneNumber;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }

}
