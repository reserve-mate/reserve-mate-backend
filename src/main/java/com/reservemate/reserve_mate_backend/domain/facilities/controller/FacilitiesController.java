package com.reservemate.reserve_mate_backend.domain.facilities.controller;

import com.reservemate.reserve_mate_backend.domain.facilities.dto.FacilitiesDto;
import com.reservemate.reserve_mate_backend.domain.facilities.service.FacilitiesService;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facilities")
public class FacilitiesController {
    private final FacilitiesService facilitiesService;
    public FacilitiesController(FacilitiesService facilitiesService) {
        this.facilitiesService = facilitiesService;
    }

    @PostMapping("/create")
    public ResponseEntity<FacilitiesDto> createFacility(@RequestBody FacilitiesDto facilitiesDto){
        return ResponseEntity.ok(null);
    }

    @GetMapping("/load")
    public ResponseEntity<List<FacilitiesDto>> getFacilities(){
        return ResponseEntity.ok(null);
    }

    @GetMapping("/load/{id}")
    public ResponseEntity<FacilitiesDto> getById(@PathVariable Long id){
        return ResponseEntity.ok(null);
    }
}
