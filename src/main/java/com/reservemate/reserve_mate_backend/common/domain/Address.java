package com.reservemate.reserve_mate_backend.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Column(name = "zipcode", length = 10)
    private String zipcode;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "district", length = 50)
    private String district;

    @Column(name = "street_address", length = 100)
    private String streetAddress;

    @Column(name = "detail_address", length = 100)
    private String detailAddress;

    @Builder
    public Address(
        String zipcode,
        String city,
        String district,
        String streetAddress,
        String detailAddress) {
        this.zipcode = zipcode;
        this.city = city;
        this.district = district;
        this.streetAddress = streetAddress;
        this.detailAddress = detailAddress;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (city != null)
            sb.append(city).append(" ");
        if (district != null)
            sb.append(district).append(" ");
        if (streetAddress != null)
            sb.append(streetAddress).append(" ");
        if (detailAddress != null)
            sb.append(detailAddress);
        return sb.toString().trim();
    }
}
