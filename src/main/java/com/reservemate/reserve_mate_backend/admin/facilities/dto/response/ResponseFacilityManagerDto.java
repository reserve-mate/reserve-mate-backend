package com.reservemate.reserve_mate_backend.admin.facilities.dto.response;

import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ResponseFacilityManagerDto {

    private Long id;
    private String assignedAt;
    private Long facilityId;
    private Long userId;
    private String managerRole;
    private String userName;
    private String email;
    private String phone;

    @Builder
    public ResponseFacilityManagerDto(Long id, String assignedAt, Long facilityId,
        Long userId, String managerRole, String userName, String email, String phone) {
        this.id = id;
        this.assignedAt = assignedAt;
        this.facilityId = facilityId;
        this.userId = userId;
        this.managerRole = managerRole;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
    }

    public static ResponseFacilityManagerDto convertFacilityManager(FacilityManager facilityManager) {
        return new ResponseFacilityManagerDto(
            facilityManager.getId(),
            facilityManager.getAssignedAt().toString(),
            facilityManager.getFacility().getId(),
            facilityManager.getUser().getId(),
            facilityManager.getManagerRole().name(),
            facilityManager.getUser().getName(),
            facilityManager.getUser().getEmail(),
            facilityManager.getUser().getPhone()
        );
    }
}
