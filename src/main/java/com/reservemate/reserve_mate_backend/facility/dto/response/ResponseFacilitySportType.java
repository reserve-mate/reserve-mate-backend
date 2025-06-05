package com.reservemate.reserve_mate_backend.facility.dto.response;

import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseFacilitySportType {

    private Long id;
    private String name;
    private SportType sportType;

    @Builder
    public ResponseFacilitySportType(Long id, String name, SportType sportType) {
        this.id = id;
        this.name = name;
        this.sportType = sportType;
    }

    public static ResponseFacilitySportType getNameAndSportType(Facility facility) {
        return ResponseFacilitySportType.builder()
            .id(facility.getId())
            .name(facility.getName())
            .sportType(facility.getSportType())
            .build();
    }

}
