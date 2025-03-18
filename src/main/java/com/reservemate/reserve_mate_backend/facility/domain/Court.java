package com.reservemate.reserve_mate_backend.facility.domain;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "courts")
@SQLDelete(sql = "UPDATE courts SET deleted = true WHERE court_id = ?")
@Where(clause = "deleted = false")
public class Court extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "court_id", updatable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "sport_type", nullable = false)
    private SportType sportType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "indoor", nullable = false)
    private boolean indoor;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Builder
    public Court(
            String name,
            SportType sportType,
            String description,
            Integer capacity,
            Boolean indoor,
            Facility facility) {
        this.name = name;
        this.sportType = sportType;
        this.description = description;
        this.capacity = capacity;
        this.indoor = indoor != null ? indoor : false;
        this.facility = facility;
    }

    public void update(
            String name,
            SportType sportType,
            String description,
            Integer capacity,
            Boolean indoor) {
        this.name = name != null ? name : this.name;
        this.sportType = sportType != null ? sportType : this.sportType;
        this.description = description != null ? description : this.description;
        this.capacity = capacity != null ? capacity : this.capacity;
        this.indoor = indoor != null ? indoor : this.indoor;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
