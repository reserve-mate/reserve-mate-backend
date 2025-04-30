package com.reservemate.reserve_mate_backend.admin.facilities.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservemate.reserve_mate_backend.admin.facilities.dto.RequestCreateFacility;
import com.reservemate.reserve_mate_backend.admin.facilities.dto.RequestFacilityImageUploadDto;
import com.reservemate.reserve_mate_backend.admin.facilities.service.AdminFacilityService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/facilities")
public class AdminFacilityController {

  private final AdminFacilityService adminFacilityService;
  private final ObjectMapper objectMapper;

  public AdminFacilityController(AdminFacilityService adminFacilityService,
      ObjectMapper objectMapper) {
    this.adminFacilityService = adminFacilityService;
    this.objectMapper = objectMapper;
  }

  @GetMapping
//  @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
  public ResponseEntity<?> getAdminFacilities() {
    return ResponseEntity.ok("관리자만 접근 가능합니다.");
  }

  @PostMapping
//  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> createFacility(@RequestPart("facilityData") RequestCreateFacility requestCreateFacility,
                                        @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                        @RequestPart(value="imageMeta", required = false) List<RequestFacilityImageUploadDto> facilityImageUploadDtoList) {
    System.out.println(requestCreateFacility.getAddress());
    System.out.println(facilityImageUploadDtoList);
    adminFacilityService.createFacility(requestCreateFacility, images, facilityImageUploadDtoList);
    return ResponseEntity.ok().build();
  }

}
