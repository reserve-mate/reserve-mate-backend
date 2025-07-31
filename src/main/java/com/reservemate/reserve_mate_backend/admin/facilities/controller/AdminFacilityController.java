package com.reservemate.reserve_mate_backend.admin.facilities.controller;

import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestAssignManagersDto;
import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestCreateCourtDto;
import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestCreateFacilityDto;
import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestFacilityImageUploadDto;
import com.reservemate.reserve_mate_backend.admin.facilities.dto.response.ResponseFacilityManagerDto;
import com.reservemate.reserve_mate_backend.admin.facilities.service.AdminCourtService;
import com.reservemate.reserve_mate_backend.admin.facilities.service.AdminFacilityService;
import com.reservemate.reserve_mate_backend.common.auth.service.CustomUserDetails;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/dashboardFacilities")
    public ResponseEntity<List<FacilityDto>> getDashboardFacilities(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(adminFacilityService.getDashboardFacilities(customUserDetails.getId()));
    }

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
        @RequestParam(name = "sportType", required = false) SportType sportType) {
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
    public ResponseEntity<?> createFacility(
        @RequestPart("facilityData") RequestCreateFacilityDto requestCreateFacilityDto,
        @RequestPart(value = "images", required = false) List<MultipartFile> images,
        @RequestPart(value = "imageMeta", required = false) List<RequestFacilityImageUploadDto> facilityImageUploadDtoList,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        adminFacilityService.createFacility(requestCreateFacilityDto, images, facilityImageUploadDtoList,
            customUserDetails);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ADMIN','FACILITY_MANAGER')")
    public ResponseEntity<?> detailAdminFacility(@PathVariable Long id) {
        return ResponseEntity.ok(adminFacilityService.detailAdminFacility(id));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateFacility(@PathVariable Long id,
        @RequestBody RequestCreateFacilityDto requestUpdateFacilityDto) {
        adminFacilityService.updateFacility(id, requestUpdateFacilityDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteFacility(@PathVariable Long id) {
        adminFacilityService.deleteFacility(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/create/court")
    public ResponseEntity<?> createCourt(@PathVariable(name = "id") Long facilityId,
        @RequestBody RequestCreateCourtDto createCourt) {
        adminFacilityService.createCourt(facilityId, createCourt);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{facilityId}/courts/{courtId}")
    public ResponseEntity<?> updateCourt(@PathVariable(name = "facilityId") Long facilityId,
        @PathVariable(name = "courtId") Long courtId,
        @RequestBody RequestCreateCourtDto requestUpdateCourtDto) {
        adminFacilityService.updateCourt(facilityId, courtId, requestUpdateCourtDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{facilityId}/courts/{courtId}")
    public ResponseEntity<?> deleteCourt(@PathVariable(name = "facilityId") Long facilityId,
        @PathVariable(name = "courtId") Long courtId) {
        adminFacilityService.deleteCourt(facilityId, courtId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{facilityId}/assign/manager")
    public ResponseEntity<?> assignManager(@PathVariable(name = "facilityId") Long id,
        @RequestBody RequestAssignManagersDto assignManagersDto) {
        adminFacilityService.assignManager(id, assignManagersDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{facilityId}/get/managerList")
    public ResponseEntity<List<ResponseFacilityManagerDto>> getFacilityManagerList(
        @PathVariable(name = "facilityId") Long id) {
        return ResponseEntity.ok(adminFacilityService.getFacilityManagerList(id));
    }

    @DeleteMapping("/{facilityId}/managers/{managerId}")
    public ResponseEntity<?> removeFacilityManager(@PathVariable(name = "facilityId") Long facilityId,
        @PathVariable(name = "managerId") Long id) {
        adminFacilityService.removeFacilityManager(facilityId, id);
        return ResponseEntity.ok().build();
    }
}
