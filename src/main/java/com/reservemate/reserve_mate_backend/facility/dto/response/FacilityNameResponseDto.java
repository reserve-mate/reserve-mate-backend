package com.reservemate.reserve_mate_backend.facility.dto.response;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.domain.OperatingHour;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FacilityNameResponseDto {

    private Long facilityId;
    private String facilityName;
    private String address;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean holiday;

    // 데이터 전달
    public static List<FacilityNameResponseDto> getFacilities(List<FacilityManager> facilityManagers,
        SportType sportType, List<OperatingHour> hours) {

        List<FacilityNameResponseDto> facilityNameResponseDtos = new ArrayList<>();

        facilityManagers.forEach((facilityManager) -> {
            if (facilityManager.getFacility().getSportType() == sportType) {
                facilityNameResponseDtos.add(toFacilityNameResponseDto(facilityManager.getFacility(), hours));
            }
        });

        return facilityNameResponseDtos;
    }

    private static FacilityNameResponseDto toFacilityNameResponseDto(Facility facility, List<OperatingHour> hours) {

        String fullAddress = facility.getAddress().getFullAddress();
        LocalTime openTime = null;
        LocalTime endTime = null;
        boolean holiday = false;

        for (OperatingHour operatingHour : hours) {
            if (facility.getId() == operatingHour.getFacility().getId()) {
                openTime = operatingHour.getOpenTime();
                endTime = operatingHour.getCloseTime();
                holiday = operatingHour.isHoliday();
            }
        }

        return FacilityNameResponseDto.builder()
            .facilityId(facility.getId())
            .facilityName(facility.getName())
            .address(fullAddress)
            .startTime(openTime)
            .endTime(endTime)
            .holiday(holiday)
            .build();
    }

}
