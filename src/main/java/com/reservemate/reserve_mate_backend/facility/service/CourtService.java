package com.reservemate.reserve_mate_backend.facility.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.dto.response.CourtNamsResponseDto;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourtService {

    private final FacilityRepository facilityRepository;
    private final CourtRepository courtRepository;

    /* 코트 이름 가져오기 */
    public List<CourtNamsResponseDto> getCourtNames(Long facilityId) {
        Facility facility = facilityRepository.findById(facilityId)
            .orElseThrow(() -> new ApiException(ErrorCode.INVALID_INPUT_VALUE));

        List<Court> courts = courtRepository.findByFacilityAndActive(facility, true);

        return CourtNamsResponseDto.toCourtNamsResponseDtos(courts);
    }

}
