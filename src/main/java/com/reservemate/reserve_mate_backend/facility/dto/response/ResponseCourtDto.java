package com.reservemate.reserve_mate_backend.facility.dto.response;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.CourtType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseCourtDto {

    private Long id;
    private String name;
    private CourtType courtType;
    private int width;
    private int height;
    private Boolean indoor;
    private Boolean active;
    private int fee;

    @Builder
    public ResponseCourtDto(Long id, String name, CourtType courtType, int width, int height,
        boolean indoor, boolean active, int fee) {
        this.id = id;
        this.name = name;
        this.courtType = courtType;
        this.width = width;
        this.height = height;
        this.indoor = indoor;
        this.active = active;
        this.fee = fee;
    }

    public static ResponseCourtDto getCourt(Court court) {
        return ResponseCourtDto.builder()
            .id(court.getId())
            .name(court.getName())
            .courtType(court.getCourtType())
            .width(court.getWidth())
            .height(court.getHeight())
            .indoor(court.isIndoor())
            .active(court.isActive())
            .fee(court.getFee())
            .build();
    }

}
