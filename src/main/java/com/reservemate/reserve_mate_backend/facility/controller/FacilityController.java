package com.reservemate.reserve_mate_backend.facility.controller;

import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.facility.dto.request.FacilityNameRequestDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.CourtNamsResponseDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityManagerNamesDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityNameResponseDto;
import com.reservemate.reserve_mate_backend.facility.service.CourtService;
import com.reservemate.reserve_mate_backend.facility.service.FacilityManagerService;
import com.reservemate.reserve_mate_backend.facility.service.FacilityService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/facility")
public class FacilityController {

    private final FacilityService facilityService;
    private final CourtService courtService;
    private final FacilityManagerService facilityManagerService;

    @GetMapping("/getManagerNames")
    public ResponseEntity<List<FacilityManagerNamesDto>> getFacilityManagers(
        @RequestParam("facilityId") Long facilityId) {
        return ResponseEntity.ok(facilityManagerService.getFacilityManagers(facilityId));
    }

    @GetMapping("/getCourtNames")
    public ResponseEntity<List<CourtNamsResponseDto>> getCourtNames(@RequestParam("facilityId") Long facilityId) {
        return ResponseEntity.ok(courtService.getCourtNames(facilityId));
    }

    @GetMapping("/getFacilityNames")
    public ResponseEntity<List<FacilityNameResponseDto>> getMatchFacilityNames(
        @RequestBody FacilityNameRequestDto nameRequestDto) {
        return ResponseEntity.ok(facilityService.getMatchFacilityNames(nameRequestDto));
    }

}
