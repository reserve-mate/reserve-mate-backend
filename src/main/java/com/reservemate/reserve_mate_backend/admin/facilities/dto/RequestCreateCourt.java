package com.reservemate.reserve_mate_backend.admin.facilities.dto;

import com.reservemate.reserve_mate_backend.facility.domain.CourtType;
import lombok.Getter;

@Getter
public class RequestCreateCourt {

    private String name;
    private CourtType courtType;
    private int width;
    private int height;
    private Boolean indoor;
    private Boolean active;
}
