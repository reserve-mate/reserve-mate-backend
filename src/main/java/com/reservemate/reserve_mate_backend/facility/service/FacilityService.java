package com.reservemate.reserve_mate_backend.facility.service;

import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.dto.FacilityDto;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<FacilityDto> loadAll(){
        List<Facility> all = facilityRepository.findAll();
        List<FacilityDto> dtos = new ArrayList<>();
        for (Facility facility : all) {
            FacilityDto dto = new FacilityDto(facility.getId(), facility.getName(), facility.getDescription(),facility.getAddress(), facility.getContactPhone());
            dtos.add(dto);
        }
        return dtos;
    }

    public Optional<FacilityDto> loadById(Long id) {
        return facilityRepository.findById(id)
                .map(facility -> new FacilityDto(
                        facility.getId(),
                        facility.getName(),
                        facility.getDescription(),
                        facility.getAddress(),
                        facility.getContactPhone()
                ));
    }
}
