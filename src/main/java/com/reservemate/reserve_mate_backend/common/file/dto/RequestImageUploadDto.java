package com.reservemate.reserve_mate_backend.common.file.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class RequestImageUploadDto {

    private MultipartFile file;
    private String fileType;
    private String description;
}
