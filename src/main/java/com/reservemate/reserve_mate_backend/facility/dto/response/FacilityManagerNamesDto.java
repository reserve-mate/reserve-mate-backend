package com.reservemate.reserve_mate_backend.facility.dto.response;

import java.util.List;

import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;

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
public class FacilityManagerNamesDto {

    private Long managerId;
    private String managerName;
    private String managerEmail;

    public static List<FacilityManagerNamesDto> toManagerNamesDtos(List<FacilityManager> managers) {

        List<FacilityManagerNamesDto> namesDtos = managers.stream()
            .map(FacilityManagerNamesDto::toFacilityManagerNamesDto).toList();

        return namesDtos;
    }

    private static FacilityManagerNamesDto toFacilityManagerNamesDto(FacilityManager manager) {
        FacilityManagerNamesDto namesDto = FacilityManagerNamesDto.builder()
            .managerId(manager.getId())
            .managerName(manager.getUser().getName())
            .managerEmail(manager.getUser().getEmail())
            .build();

        return namesDto;
    }

}
