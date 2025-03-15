package com.reservemate.reserve_mate_backend.facilities.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FacilitiesDto {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String latitude;
    private String longitude;
    private String contactPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
