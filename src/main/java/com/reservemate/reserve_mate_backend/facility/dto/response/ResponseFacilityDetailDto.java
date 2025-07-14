package com.reservemate.reserve_mate_backend.facility.dto.response;

import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import java.time.DayOfWeek;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseFacilityDetailDto {

    private Long facilityId;

    private String facilityName;
    private SportType sportType;
    private String address;
    private String description;
    private DayOfWeek dayOfWeek;

    private String openTime;
    private String closeTime;
    private boolean holiday;
    private List<ResponseCourtDto> courts;

    private List<ResponseReviewFacilityDto> reviews;

    private String managerPhoneNumber;

    private String imageUrl;

    @Builder
    public ResponseFacilityDetailDto(Long facilityId, String facilityName, SportType sportType,
        String address, String description, DayOfWeek dayOfWeek, String openTime, String closeTime,
        boolean holiday, List<ResponseCourtDto> courts, List<ResponseReviewFacilityDto> reviews,
        String managerPhoneNumber, String imageUrl) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.sportType = sportType;
        this.address = address;
        this.description = description;
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.holiday = holiday;
        this.courts = courts;
        this.reviews = reviews;
        this.managerPhoneNumber = managerPhoneNumber;
        this.imageUrl = imageUrl;
    }

}
