package com.reservemate.reserve_mate_backend.admin.facilities.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class RequestFacilityImageUploadDto {
    private MultipartFile file;
    private String fileType;
    private String description;
    private boolean main;
    private Integer displayOrder;
}
