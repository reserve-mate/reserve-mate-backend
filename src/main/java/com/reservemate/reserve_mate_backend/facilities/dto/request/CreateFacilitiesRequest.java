package com.reservemate.reserve_mate_backend.facilities.dto.request;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateFacilitiesRequest {
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
