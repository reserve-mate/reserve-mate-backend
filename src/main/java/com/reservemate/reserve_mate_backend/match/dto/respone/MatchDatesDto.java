package com.reservemate.reserve_mate_backend.match.dto.respone;

import java.util.List;

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
public class MatchDatesDto {

    private List<MatchDateDto> dateDtos;

}
