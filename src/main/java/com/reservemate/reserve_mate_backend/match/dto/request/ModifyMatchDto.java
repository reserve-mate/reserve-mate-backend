package com.reservemate.reserve_mate_backend.match.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ModifyMatchDto {

    private Integer teamCapacity;
    private String description;

}
