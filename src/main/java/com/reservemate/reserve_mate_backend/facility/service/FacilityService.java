package com.reservemate.reserve_mate_backend.facility.service;

import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.dto.FacilityDto;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    public void create(FacilityDto dto){
        Facility facility = Facility.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .address(dto.getAddress())
                .contactPhone(dto.getContactPhone())
                .build();
        facilityRepository.save(facility);
    }
}
