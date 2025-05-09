package com.reservemate.reserve_mate_backend.match.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.reservemate.reserve_mate_backend.admin.match.dto.request.AdminMatchesRequest;
import com.reservemate.reserve_mate_backend.admin.match.dto.response.AdminMatchesResponse;
import com.reservemate.reserve_mate_backend.match.dto.request.MatchSearchDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchDateDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchesDto;

public interface MatchCustomRepository {

    Slice<MatchesDto> getMatches(Pageable pageable, MatchSearchDto matchSearchDto);

    List<MatchDateDto> getMatchesForDate(MatchSearchDto matchSearchDto);

    // 관리자 관점 매치 조회
    List<AdminMatchesResponse> getAdminMatches(Long userId, AdminMatchesRequest adminMatchesRequest);

}
