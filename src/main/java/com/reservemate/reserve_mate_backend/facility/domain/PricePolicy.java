package com.reservemate.reserve_mate_backend.facility.domain;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "price_policies")
@SQLDelete(sql = "UPDATE price_policies SET deleted = true WHERE price_policy_id = ?")
public class PricePolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_policy_id", updatable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_type", nullable = false)
    private DayType dayType;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "minimum_hours")
    private Integer minimumHours;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id")
    private Court court;

    @Builder
    public PricePolicy(
        String name,
        DayType dayType,
        LocalTime startTime,
        LocalTime endTime,
        Integer price,
        Integer minimumHours,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        Facility facility,
        Court court) {
        this.name = name;
        this.dayType = dayType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.minimumHours = minimumHours;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.facility = facility;
        this.court = court;
    }

    public void update(
        String name,
        DayType dayType,
        LocalTime startTime,
        LocalTime endTime,
        Integer price,
        Integer minimumHours,
        LocalDate effectiveFrom,
        LocalDate effectiveTo) {
        this.name = name != null ? name : this.name;
        this.dayType = dayType != null ? dayType : this.dayType;
        this.startTime = startTime != null ? startTime : this.startTime;
        this.endTime = endTime != null ? endTime : this.endTime;
        this.price = price != null ? price : this.price;
        this.minimumHours = minimumHours != null ? minimumHours : this.minimumHours;
        this.effectiveFrom = effectiveFrom != null ? effectiveFrom : this.effectiveFrom;
        this.effectiveTo = effectiveTo != null ? effectiveTo : this.effectiveTo;
    }
}
