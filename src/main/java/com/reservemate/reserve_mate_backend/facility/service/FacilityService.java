package com.reservemate.reserve_mate_backend.facility.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.domain.OperatingHour;
import com.reservemate.reserve_mate_backend.facility.dto.request.FacilityNameRequestDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityNameResponseDto;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
// import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;
import com.reservemate.reserve_mate_backend.facility.repository.OperationHourRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacilityService {

    //private final FacilityRepository facilityRepository;
    private final FacilityManagerRepository facilityManagerRepository;
    private final OperationHourRepository hourRepository;

    public List<FacilityNameResponseDto> getMatchFacilityNames(FacilityNameRequestDto nameRequestDto) {

        List<FacilityManager> facilityManagers = facilityManagerRepository.findByUserId(nameRequestDto.getUserId());

        List<OperatingHour> hours = hourRepository.findByFacilityInAndDayOfWeek(
            FacilityManager.getFacilityIds(facilityManagers), Utils.getDayOfWeek());

        return FacilityNameResponseDto.getFacilities(facilityManagers, nameRequestDto.getSportType(), hours);
    }

}
