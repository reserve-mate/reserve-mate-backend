package com.reservemate.reserve_mate_backend.match.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.reservemate.reserve_mate_backend.match.dto.request.MatchSearchDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchDateDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchesDto;

public interface MatchCustomRepository {

    Slice<MatchesDto> getMatches(Pageable pageable, MatchSearchDto matchSearchDto);

    List<MatchDateDto> getMatchesForDate(MatchSearchDto matchSearchDto);

}
