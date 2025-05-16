package com.reservemate.reserve_mate_backend.facility.repository;

import com.reservemate.reserve_mate_backend.facility.dto.request.RequestFacilitySearchDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomFacilityRepository {

    Slice<FacilityDto> findAllByCursor(RequestFacilitySearchDto searchDto, Pageable pageable);
}
