package com.reservemate.reserve_mate_backend.facility.domain;

import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Embedded
    private Address address;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Builder
    public Facility(String name, String description, Address address, String contactPhone) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.contactPhone = contactPhone;
    }

    public void update(String name, String description, Address address, String contactPhone) {
        this.name = name != null ? name : this.name;
        this.description = description != null ? description : this.description;
        this.address = address != null ? address : this.address;
        this.contactPhone = contactPhone != null ? contactPhone : this.contactPhone;
    }
}
