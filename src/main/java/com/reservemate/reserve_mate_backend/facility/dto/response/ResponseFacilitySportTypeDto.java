package com.reservemate.reserve_mate_backend.facility.dto.response;

import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseFacilitySportTypeDto {

    private Long id;
    private String name;
    private SportType sportType;

    @Builder
    public ResponseFacilitySportTypeDto(Long id, String name, SportType sportType) {
        this.id = id;
        this.name = name;
        this.sportType = sportType;
    }

    public static ResponseFacilitySportTypeDto getNameAndSportType(Facility facility) {
        return ResponseFacilitySportTypeDto.builder()
            .id(facility.getId())
            .name(facility.getName())
            .sportType(facility.getSportType())
            .build();
    }

}
