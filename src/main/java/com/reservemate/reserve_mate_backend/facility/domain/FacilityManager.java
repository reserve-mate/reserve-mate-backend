package com.reservemate.reserve_mate_backend.facility.domain;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import com.reservemate.reserve_mate_backend.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "facility_managers")
@SQLDelete(sql = "UPDATE facility_managers SET deleted = true WHERE facility_manager_id = ?")
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

    @Builder
    public FacilityManager(Facility facility, User user) {
        this.assignedAt = LocalDateTime.now();
        this.facility = facility;
        this.user = user;
    }

    public FacilityManager(Long id, Facility facility, User user) {
        this.id = id;
        this.assignedAt = LocalDateTime.now();
        this.facility = facility;
        this.user = user;
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
}
