package com.reservemate.reserve_mate_backend.facility.repository;

import com.reservemate.reserve_mate_backend.facility.dto.request.RequestFacilitySearchDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseFacilitiesDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseFacilityDetailDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomFacilityRepository {

    Slice<FacilityDto> findAllByCursor(RequestFacilitySearchDto searchDto, Pageable pageable);

    Slice<ResponseFacilitiesDto> findAllCourtsByCursor(RequestFacilitySearchDto facilitySearchDto, Pageable pageable);

    ResponseFacilityDetailDto findFacilityDetailByCourtId(Long id);
}
