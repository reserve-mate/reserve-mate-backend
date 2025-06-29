package com.reservemate.reserve_mate_backend.facility.service;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseCourtDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseFacilitySportTypeDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ReviewFacilitResponse;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final CourtRepository courtRepository;

    /* 리뷰 작성 시 시설 정보 가져오기 */
    public ReviewFacilitResponse getReviewFacility(Long facilityId) {
        Facility facility = facilityRepository.findById(facilityId).orElseThrow(() -> new ApiException(
            ErrorCode.INVALID_INPUT_VALUE));
        return ReviewFacilitResponse.toReviewFacilitResponse(facility);
    }

    public List<ResponseCourtDto> getCourtList(Long id) {
        Facility facility = facilityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("해당 시설이 존재하지 않습니다."));
        List<Court> courts = courtRepository.findByFacility(facility);

        return courts.stream()
            .map(ResponseCourtDto::getCourt)
            .toList();
    }

    public ResponseFacilitySportTypeDto getFacilityNameAndSportType(Long id) {
        Facility facility = facilityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("해당 시설이 존재하지 않습니다."));
        return ResponseFacilitySportTypeDto.getNameAndSportType(facility);
    }
}
