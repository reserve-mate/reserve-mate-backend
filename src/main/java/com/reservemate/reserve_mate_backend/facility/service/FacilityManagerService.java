package com.reservemate.reserve_mate_backend.facility.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityManagerNamesDto;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacilityManagerService {

    private final FacilityManagerRepository facilityManagerRepository;
    private final FacilityRepository facilityRepository;

    /* 매니져 이름 목록 조회 */
    public List<FacilityManagerNamesDto> getFacilityManagers(Long facilityId) {

        Facility facility = facilityRepository.findById(facilityId)
            .orElseThrow(() -> new ApiException(ErrorCode.INVALID_INPUT_VALUE));

        List<FacilityManager> managers = facilityManagerRepository.findByFacility(facility);

        return FacilityManagerNamesDto.toManagerNamesDtos(managers);
    }

}
