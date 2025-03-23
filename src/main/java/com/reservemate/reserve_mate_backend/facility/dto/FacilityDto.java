package com.reservemate.reserve_mate_backend.facility.dto;

import com.reservemate.reserve_mate_backend.common.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityDto {
    private Long id;
    private String name;
    private String description;
    private Address address;
    private String contactPhone;
}
