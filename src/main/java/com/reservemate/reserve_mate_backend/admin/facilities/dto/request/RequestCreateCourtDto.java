package com.reservemate.reserve_mate_backend.admin.facilities.dto.request;

import com.reservemate.reserve_mate_backend.facility.domain.CourtType;
import lombok.Getter;

@Getter
public class RequestCreateCourtDto {

    private String name;
    private CourtType courtType;
    private Integer width;
    private Integer height;
    private Boolean indoor;
    private Boolean active;
    private Integer fee;
}
