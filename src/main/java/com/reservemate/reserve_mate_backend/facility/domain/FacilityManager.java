package com.reservemate.reserve_mate_backend.facility.domain;

import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestAssignManagersDto;
import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.domain.UserRole;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "facility_managers")
@SQLDelete(sql = "UPDATE facility_managers SET deleted = true WHERE facility_manager_id = ?")
@SQLRestriction("deleted = false")
public class FacilityManager extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facility_manager_id", updatable = false)
    private Long id;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "manager_role", nullable = false)
    private ManagerRole managerRole;

    @Builder
    public FacilityManager(Facility facility, User user, ManagerRole managerRole) {
        this.assignedAt = LocalDateTime.now();
        this.facility = facility;
        this.user = user;
        this.managerRole = managerRole;
    }

    @Builder
    public FacilityManager(Long id, Facility facility, User user, ManagerRole managerRole) {
        this.id = id;
        this.assignedAt = LocalDateTime.now();
        this.facility = facility;
        this.user = user;
        this.managerRole = managerRole;
    }

    // 매니저 등급 가져오기
    public boolean chkManagerRole() {

        boolean result = true;

        if (this.user.getRole() == UserRole.ROLE_FACILITY_MANAGER) {
            if (this.managerRole == ManagerRole.STAFF) {
                throw new ApiException(ErrorCode.NOT_FORBIDDEN);
            }
            result = false;
        } else if (this.user.getRole() == UserRole.ROLE_ADMIN) {
            result = true;
        }

        return result;
    }

    public Long getUserId() {
        return this.user.getId();
    }

    // 시설 id 목록 가져오기
    public static List<Long> getFacilityIds(List<FacilityManager> facilityManagers) {
        List<Long> ids = new ArrayList<>();
        facilityManagers.forEach(facilityManager -> {
            ids.add(facilityManager.facility.getId());
        });
        return ids;
    }

    public void updateRole(ManagerRole role) {
    }

    public static FacilityManager create(Facility facility, User user, RequestAssignManagersDto managersDto) {
        return FacilityManager.builder()
            .facility(facility)
            .user(user)
            .managerRole(managersDto.getManagerRole())
            .build();
    }

    public static FacilityManager create(Facility facility, User user, ManagerRole role) {
        return FacilityManager.builder()
            .facility(facility)
            .user(user)
            .managerRole(role)
            .build();
    }

    /* 현재 매니저가 STAFF인지 검증 */
    public void isStaff() {
        if (this.managerRole == ManagerRole.STAFF) {
            throw new ApiException(ErrorCode.NOT_FORBIDDEN);
        }
    }
}
