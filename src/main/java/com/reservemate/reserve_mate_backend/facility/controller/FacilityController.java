package com.reservemate.reserve_mate_backend.facility.controller;

import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseCourtDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseFacilitySportTypeDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ReviewFacilitResponse;
import com.reservemate.reserve_mate_backend.facility.service.FacilityService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/facility")
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping("/review/{facilityId}")
    public ResponseEntity<ReviewFacilitResponse> getReviewFacility(@PathVariable("facilityId") Long facilityId) {
        return ResponseEntity.ok(facilityService.getReviewFacility(facilityId));
    }

    @GetMapping("/name/type/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    public ResponseEntity<ResponseFacilitySportTypeDto> getFacilityNameAndSportType(@PathVariable Long id) {
        return ResponseEntity.ok(facilityService.getFacilityNameAndSportType(id));
    }

    @GetMapping("/{id}/courts")
//    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    public ResponseEntity<List<ResponseCourtDto>> getCourtList(@PathVariable Long id) {
        return ResponseEntity.ok(facilityService.getCourtList(id));
    }

    @GetMapping("/list")
    public ResponseEntity<?> searchFacilities(@RequestParam(required = false) String sportType,
        @RequestParam(defaultValue = "0") int minPrice, @RequestParam(defaultValue = "9999999") int maxPrice,
        @RequestParam(required = false) String keyword, @RequestParam(required = false, defaultValue = "0") Long lastId,
        Pageable pageable) {
        return facilityService.getSearchFacilities(sportType, minPrice, maxPrice, keyword, lastId, pageable);
    }
}
