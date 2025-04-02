package com.reservemate.reserve_mate_backend.match.repository;

import java.util.List;

import com.reservemate.reserve_mate_backend.match.dto.request.MatchSearchDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchDateDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchesDto;

public interface MatchCustomRepository {

    List<MatchesDto> getMatches(MatchSearchDto matchSearchDto);

    List<MatchDateDto> getMatchesForDate(MatchSearchDto matchSearchDto);

}
