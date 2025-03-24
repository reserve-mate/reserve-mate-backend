package com.reservemate.reserve_mate_backend.facility.controller;

import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.dto.FacilityDto;
import com.reservemate.reserve_mate_backend.facility.service.FacilityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facility")
@AllArgsConstructor
public class FacilityController {
    private final FacilityService facilityService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody FacilityDto facilitiesDto){
        facilityService.create(facilitiesDto);
        return ResponseEntity.ok(facilitiesDto);
    }

    @GetMapping("/load")
    public ResponseEntity<List<FacilityDto>> getFacilities(){
        return ResponseEntity.ok(null);
    }

    @GetMapping("/load/{id}")
    public ResponseEntity<FacilityDto> getById(@PathVariable Long id){
        return ResponseEntity.ok(null);
    }
}
