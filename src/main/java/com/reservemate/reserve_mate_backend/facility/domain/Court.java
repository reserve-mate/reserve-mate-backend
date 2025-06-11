package com.reservemate.reserve_mate_backend.facility.domain;

import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestCreateCourtDto;
import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "courts")
@SQLDelete(sql = "UPDATE courts SET deleted = true WHERE court_id = ?")
@SQLRestriction("deleted = false")
public class Court extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "court_id", updatable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourtType courtType;

    @Column(nullable = false)
    private int width;

    @Column(nullable = false)
    private int height;

    @Column(name = "indoor", nullable = false)
    private boolean indoor;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "fee", nullable = false)
    private Integer fee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Builder
    public Court(
        String name,
        CourtType courtType,
        int width,
        int height,
        Boolean indoor,
        Boolean active,
        Integer fee,
        Facility facility) {
        this.name = name;
        this.courtType = courtType;
        this.width = width;
        this.height = height;
        this.indoor = indoor != null ? indoor : false;
        this.active = active != null ? active : false;
        this.fee = fee;
        this.facility = facility;
    }

    public Court(
        Long id,
        String name,
        CourtType courtType,
        int width,
        int height,
        Boolean indoor,
        Integer fee,
        Facility facility) {
        this.id = id;
        this.name = name;
        this.courtType = courtType;
        this.width = width;
        this.height = height;
        this.indoor = indoor != null ? indoor : false;
        this.fee = fee;
        this.facility = facility;
    }

    public void update(
        String name,
        CourtType courtType,
        int width,
        int height,
        Boolean indoor,
        Boolean active,
        Integer fee) {
        this.name = name != null ? name : this.name;
        this.courtType = courtType;
        this.width = width;
        this.height = height;
        this.indoor = indoor != null ? indoor : this.indoor;
        this.active = active;
        this.fee = fee;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public static Court create(RequestCreateCourtDto dto, Facility facility) {
        return Court.builder()
            .name(dto.getName())
            .courtType(dto.getCourtType())
            .width(dto.getWidth())
            .height(dto.getHeight())
            .indoor(dto.getIndoor())
            .active(dto.getActive())
            .fee(dto.getFee())
            .facility(facility)
            .build();
    }

    // 시설 아이디 가져오기
    public Long getFacilityId() {
        return this.facility.getId();
    }

    // 시설 종목 가져오기
    public SportType getSportType() {
        return this.facility.getSportType();
    }

    // 코트 아이디 가져오기
    public static List<Long> getCourtIds(List<Court> courts) {
        List<Long> ids = new ArrayList<>();
        courts.forEach((court) -> {
            ids.add(court.getId());
        });
        return ids;
    }
}
