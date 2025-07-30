package com.reservemate.reserve_mate_backend.facility.repository;

import com.reservemate.reserve_mate_backend.facility.dto.request.RequestFacilitySearchDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.PopularFacilityResponse;
import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseFacilitiesDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseFacilityDetailDto;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomFacilityRepository {

    /* 인기 시설 목록 가져오기 */
    List<PopularFacilityResponse> findPopularFacility(List<Long> facilityIds);

    Slice<FacilityDto> findAllByCursor(RequestFacilitySearchDto searchDto, Pageable pageable);

    Slice<ResponseFacilitiesDto> findAllCourtsByCursor(RequestFacilitySearchDto facilitySearchDto, Pageable pageable);

    ResponseFacilityDetailDto findFacilityDetailByCourtId(Long id);
}
