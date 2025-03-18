package com.reservemate.reserve_mate_backend.facility.domain;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import com.reservemate.reserve_mate_backend.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ManagerRole role;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public FacilityManager(ManagerRole role, Facility facility, User user) {
        this.role = role;
        this.assignedAt = LocalDateTime.now();
        this.facility = facility;
        this.user = user;
    }

    public void updateRole(ManagerRole role) {
        this.role = role;
    }
}
