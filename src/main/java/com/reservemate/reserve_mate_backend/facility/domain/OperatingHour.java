package com.reservemate.reserve_mate_backend.facility.domain;

import com.reservemate.reserve_mate_backend.admin.facilities.dto.RequestOperatingHour;
import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "operating_hours")
@SQLDelete(sql = "UPDATE operating_hours SET deleted = true WHERE operating_hour_id = ?")
public class OperatingHour extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operating_hour_id", updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(name = "open_time", nullable = false)
    private LocalTime openTime;

    @Column(name = "close_time", nullable = false)
    private LocalTime closeTime;

    @Column(name = "is_holiday", nullable = false)
    private boolean holiday;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Builder
    public OperatingHour(
        DayOfWeek dayOfWeek,
        LocalTime openTime,
        LocalTime closeTime,
        Boolean holiday,
        Facility facility) {
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.holiday = holiday != null ? holiday : false;
        this.facility = facility;
    }

    @Builder
    public OperatingHour(
        Long id,
        DayOfWeek dayOfWeek,
        LocalTime openTime,
        LocalTime closeTime,
        Boolean holiday,
        Facility facility) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.holiday = holiday != null ? holiday : false;
        this.facility = facility;
    }

    public void update(LocalTime openTime, LocalTime closeTime, Boolean holiday) {
        this.openTime = openTime != null ? openTime : this.openTime;
        this.closeTime = closeTime != null ? closeTime : this.closeTime;
        this.holiday = holiday != null ? holiday : this.holiday;
    }

    public static OperatingHour create(RequestOperatingHour dto, Facility facility) {
        return OperatingHour.builder()
            .dayOfWeek(dto.getDayOfWeek())
            .openTime(LocalTime.parse(dto.getOpenTime()))
            .closeTime(LocalTime.parse(dto.getCloseTime()))
            .holiday(dto.getHoliday())
            .facility(facility)
            .build();
    }
}
