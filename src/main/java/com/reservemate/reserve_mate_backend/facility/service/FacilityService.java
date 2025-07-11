package com.reservemate.reserve_mate_backend.facility.service;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.facility.dto.request.RequestFacilitySearchDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseCourtDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseFacilitiesDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseFacilityDetailDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseFacilitySportTypeDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ReviewFacilitResponse;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<Slice<ResponseFacilitiesDto>> getSearchFacilities(String sportType, int minPrice,
        int maxPrice,
        String keyword, Long lastId, Pageable pageable) {
        RequestFacilitySearchDto facilitySearchDto = RequestFacilitySearchDto.builder()
            .keyword(keyword)
            .sportType(sportType != null ? SportType.valueOf(sportType) : null)
            .minPrice(minPrice)
            .maxPrice(maxPrice)
            .lastId(lastId == 0 ? null : lastId)
            .size(pageable.getPageSize())
            .build();

        Slice<ResponseFacilitiesDto> facilities = facilityRepository.findAllCourtsByCursor(facilitySearchDto, pageable);
        return ResponseEntity.ok(facilities);
    }

    public ResponseFacilityDetailDto getDetailFacilityDetail(Long id) {
        return facilityRepository.findFacilityDetailByCourtId(id);
    }
}
