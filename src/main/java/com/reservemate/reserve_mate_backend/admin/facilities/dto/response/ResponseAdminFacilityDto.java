package com.reservemate.reserve_mate_backend.admin.facilities.dto.response;

import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.OperatingHour;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseAdminFacilityDto {

    private Long id;
    private String name;
    private SportType sportType;
    private Address address;
    private boolean hasParking;
    private boolean hasShower;
    private boolean hasEquipmentRental;
    private boolean hasCafe;
    private int courtCount;

    private List<ResponseOperatingHour> operatingHours;
    private List<ResponseCourtDto> courts;

    @Builder
    public ResponseAdminFacilityDto(Long id, String name, SportType sportType, Address address,
        boolean hasParking, boolean hasShower, boolean hasEquipmentRental, boolean hasCafe, int courtCount,
        List<ResponseOperatingHour> operatingHours, List<ResponseCourtDto> courts) {
        this.id = id;
        this.name = name;
        this.sportType = sportType;
        this.address = address;
        this.hasParking = hasParking;
        this.hasShower = hasShower;
        this.hasEquipmentRental = hasEquipmentRental;
        this.hasCafe = hasCafe;
        this.courtCount = courtCount;
        this.operatingHours = operatingHours;
        this.courts = courts;
    }

    public static ResponseAdminFacilityDto getFacility(Facility facility, List<OperatingHour> operatingHours,
        List<Court> courts) {
        String conventient = facility.getConventient();
        return ResponseAdminFacilityDto.builder()
            .id(facility.getId())
            .name(facility.getName())
            .sportType(facility.getSportType())
            .address(facility.getAddress())
            .hasParking(conventient.charAt(0) == '1')
            .hasShower(conventient.charAt(1) == '1')
            .hasEquipmentRental(conventient.charAt(2) == '1')
            .hasCafe(conventient.charAt(3) == '1')
            .courtCount(courts.size())
            .operatingHours(convertOperatingHourList(operatingHours))
            .courts(convertCourtList(courts))
            .build();
    }

    public static List<ResponseOperatingHour> convertOperatingHourList(List<OperatingHour> entities) {
        return entities.stream()
            .map(ResponseOperatingHour::getOperatingHour)
            .toList();
    }

    public static List<ResponseCourtDto> convertCourtList(List<Court> courts) {
        return courts.stream()
            .map(ResponseCourtDto::getCourt)
            .toList();
    }
}
