package com.reservemate.reserve_mate_backend.facilities.controller;

import com.reservemate.reserve_mate_backend.facilities.dto.request.CreateFacilitiesRequest;
import com.reservemate.reserve_mate_backend.facilities.service.FacilitiesService;
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
    public ResponseEntity<CreateFacilitiesRequest> createFacility(@RequestBody CreateFacilitiesRequest request){
        return ResponseEntity.ok(null);
    }

    @GetMapping("/load")
    public ResponseEntity<List<CreateFacilitiesRequest>> getFacilities(){
        return ResponseEntity.ok(null);
    }

    @GetMapping("/load/{id}")
    public ResponseEntity<CreateFacilitiesRequest> getById(@PathVariable Long id){
        return ResponseEntity.ok(null);
    }
}
