package com.reservemate.reserve_mate_backend.facility.domain;

import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestOperatingHour;
import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    // 사용 가능한 시간대
    public static List<LocalTime> getAvailableHours(OperatingHour operatingHour, List<LocalTime> matchTimes,
        List<LocalTime> reserveTimes) {
        // 해당 요일 날짜 시간대 List
        List<LocalTime> operationHours = getOperationHours(operatingHour.getOpenTime(), operatingHour.getCloseTime());

        // 겹치는 시간대 List
        List<LocalTime> overLappingTimes = getOverlappingTimes(matchTimes, reserveTimes);

        return getUnionHoursWithout(operationHours, overLappingTimes);
    }

    // 사용 가능한 시간대 List
    private static List<LocalTime> getUnionHoursWithout(List<LocalTime> operationHours,
        List<LocalTime> overLappingTimes) {
        Set<LocalTime> hours = new HashSet<>(operationHours);

        Set<LocalTime> intersection = new HashSet<>(operationHours);
        intersection.retainAll(overLappingTimes);

        hours.removeAll(intersection); // 합집합에서 교집합 제거

        return hours.stream().sorted().toList();
    }

    // 겹치는 시간대 List
    private static List<LocalTime> getOverlappingTimes(List<LocalTime> matchTimes, List<LocalTime> reserveTimes) {
        Set<LocalTime> times = new HashSet<>();
        times.addAll(matchTimes);
        times.addAll(reserveTimes);

        return times.stream().sorted().toList();
    }

    // 해당 요일 날짜 시간대 List
    private static List<LocalTime> getOperationHours(LocalTime openTime, LocalTime endTime) {

        List<LocalTime> slots = new ArrayList<>();

        LocalTime time = openTime;
        while (time.isBefore(endTime)) {
            slots.add(time);
            time = time.plusHours(1);
        }

        return slots;
    }
}
