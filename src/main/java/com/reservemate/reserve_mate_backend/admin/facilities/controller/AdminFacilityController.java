package com.reservemate.reserve_mate_backend.admin.facilities.controller;

import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestCreateFacility;
import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestFacilityImageUploadDto;
import com.reservemate.reserve_mate_backend.admin.facilities.service.AdminFacilityService;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/facilities")
public class AdminFacilityController {

    private final AdminFacilityService adminFacilityService;

    public AdminFacilityController(AdminFacilityService adminFacilityService) {
        this.adminFacilityService = adminFacilityService;
    }

    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    public ResponseEntity<Slice<FacilityDto>> getAdminFacilities(@RequestParam(required = false) String keyword,
        @RequestParam(required = false, defaultValue = "0") long lastId, Pageable pageable) {

        return adminFacilityService.getAdminFacilityList(keyword, lastId, pageable);
    }

    @PostMapping
//  @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createFacility(@RequestPart("facilityData") RequestCreateFacility requestCreateFacility,
        @RequestPart(value = "images", required = false) List<MultipartFile> images,
        @RequestPart(value = "imageMeta", required = false) List<RequestFacilityImageUploadDto> facilityImageUploadDtoList) {

        adminFacilityService.createFacility(requestCreateFacility, images, facilityImageUploadDtoList);
        return ResponseEntity.ok().build();
    }

}
