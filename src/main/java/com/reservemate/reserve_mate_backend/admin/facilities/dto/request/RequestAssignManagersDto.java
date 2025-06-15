package com.reservemate.reserve_mate_backend.admin.facilities.dto.request;

import com.reservemate.reserve_mate_backend.facility.domain.ManagerRole;
import lombok.Getter;

@Getter
public class RequestAssignManagersDto {

    private String userName;
    private String email;
    private ManagerRole managerRole;
}
