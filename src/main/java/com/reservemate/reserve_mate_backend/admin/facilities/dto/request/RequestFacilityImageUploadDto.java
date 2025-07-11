package com.reservemate.reserve_mate_backend.admin.facilities.dto.request;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class RequestFacilityImageUploadDto {

    private MultipartFile file;

    private String fileType;
    private String description;
    private boolean isMain;
    private Integer displayOrder;

    @Builder
    public RequestFacilityImageUploadDto(MultipartFile file, String fileType, String description,
        boolean isMain, Integer displayOrder) {
        this.file = file;
        this.fileType = fileType;
        this.description = description;
        this.isMain = isMain;
        this.displayOrder = displayOrder;
    }
}
