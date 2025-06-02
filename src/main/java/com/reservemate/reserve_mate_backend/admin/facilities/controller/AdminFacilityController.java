package com.reservemate.reserve_mate_backend.admin.facilities.controller;

import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestCreateFacility;
import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestFacilityImageUploadDto;
import com.reservemate.reserve_mate_backend.admin.facilities.dto.response.ResponseCourtDto;
import com.reservemate.reserve_mate_backend.admin.facilities.service.AdminCourtService;
import com.reservemate.reserve_mate_backend.admin.facilities.service.AdminFacilityService;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.facility.dto.response.CourtNamsResponseDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityManagerNamesDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityNameResponseDto;
import com.reservemate.reserve_mate_backend.facility.service.FacilityManagerService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/facilities")
@RequiredArgsConstructor
public class AdminFacilityController {

    private final AdminFacilityService adminFacilityService;
    private final FacilityManagerService facilityManagerService;
    private final AdminCourtService adminCourtService;

    /* 매치 등록 시 매니저 조회 */
    @GetMapping("/getManagerNames")
    public ResponseEntity<List<FacilityManagerNamesDto>> getFacilityManagers(
        @RequestParam("facilityId") Long facilityId) {
        return ResponseEntity.ok(facilityManagerService.getFacilityManagers(facilityId));
    }

    /* 매치 등록 시 코드명 조회 */
    @GetMapping("/getCourtNames")
    public ResponseEntity<List<CourtNamsResponseDto>> getCourtNames(@RequestParam("facilityId") Long facilityId) {
        return ResponseEntity.ok(adminCourtService.getCourtNames(facilityId));
    }

    /* 매치 등록 시 시설명 조회 */
    @GetMapping("/getFacilityNames")
    public ResponseEntity<List<FacilityNameResponseDto>> getMatchFacilityNames(HttpServletRequest request,
        @RequestParam(name = "sportType") SportType sportType) {
        return ResponseEntity.ok(adminFacilityService.getMatchFacilityNames(request, sportType));
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

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    public ResponseEntity<?> detailAdminFacility(@PathVariable Long id) {
        return ResponseEntity.ok(adminFacilityService.detailAdminFacility(id));
    }

    @GetMapping("/get/court/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    public ResponseEntity<List<ResponseCourtDto>> getAdminCourtList(@PathVariable Long id) {
        List<ResponseCourtDto> courts = adminFacilityService.getAdminCourtList(id);
        return ResponseEntity.ok(courts);
    }
}
