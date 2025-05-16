package com.reservemate.reserve_mate_backend.facility.domain;

import com.reservemate.reserve_mate_backend.admin.facilities.dto.request.RequestCreateFacility;
import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "facilities")
@SQLDelete(sql = "UPDATE facilities SET deleted = true WHERE facility_id = ?")
public class Facility extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facility_id", updatable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SportType sportType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Embedded
    private Address address;

    @Column(name = "conventient", nullable = false)
    @ColumnDefault("0000")
    private String conventient;

    @Builder
    public Facility(String name, SportType sportType, String description, Address address, String conventient) {
        this.name = name;
        this.sportType = sportType;
        this.description = description;
        this.address = address;
        this.conventient = conventient;
    }

    @Builder
    public Facility(Long id, String name, SportType sportType, String description, Address address,
        String conventient) {
        this.id = id;
        this.name = name;
        this.sportType = sportType;
        this.description = description;
        this.address = address;
        this.conventient = conventient;
    }

    public void update(String name, String description, Address address) {
        this.name = name != null ? name : this.name;
        this.description = description != null ? description : this.description;
        this.address = address != null ? address : this.address;
    }

    public static String setConventient(boolean parking, boolean shower, boolean rental,
        boolean cafe) {
        return (parking ? "1" : "0") +
            (shower ? "1" : "0") +
            (rental ? "1" : "0") +
            (cafe ? "1" : "0");
    }

    public static Facility create(RequestCreateFacility dto) {
        String conventient = setConventient(dto.isHasParking(), dto.isHasShower(), dto.isHasEquipmentRental(), dto
            .isHasCafe());
        return Facility.builder()
            .name(dto.getName())
            .sportType(dto.getSportType())
            .description(dto.getDescription())
            .address(dto.getAddress())
            .conventient(conventient)
            .build();
    }
}
