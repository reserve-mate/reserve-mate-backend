package com.reservemate.reserve_mate_backend.facility.dto.response;

import java.util.List;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.CourtType;

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
public class CourtNamsResponseDto {

    private Long courtId;
    private String courtName;
    private CourtType courtType;

    public static List<CourtNamsResponseDto> toCourtNamsResponseDtos(List<Court> courts) {
        return courts.stream()
            .map(CourtNamsResponseDto::toCourtNamsResponseDto).toList();
    }

    private static CourtNamsResponseDto toCourtNamsResponseDto(Court court) {
        return CourtNamsResponseDto.builder()
            .courtId(court.getId())
            .courtName(court.getName())
            .courtType(court.getCourtType())
            .build();
    }

}
