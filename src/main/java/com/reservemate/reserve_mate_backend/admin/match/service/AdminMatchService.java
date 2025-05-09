package com.reservemate.reserve_mate_backend.admin.match.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.admin.match.dto.request.AdminMatchesRequest;
import com.reservemate.reserve_mate_backend.admin.match.dto.response.AdminMatchesResponse;
import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchCustomRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMatchService {

    private final MatchCustomRepository matchCustomRepository;
    private final JwtUtil jwtUtil;

    /* 관리자 매치 목록 조회 */
    @Transactional
    public List<AdminMatchesResponse> getMatches(HttpServletRequest request, AdminMatchesRequest adminMatchesRequest) {

        String accessToken = request.getHeader("access");
        Long userId = jwtUtil.getId(accessToken);

        List<AdminMatchesResponse> matchesResponses = matchCustomRepository.getAdminMatches(userId,
            adminMatchesRequest);

        return matchesResponses;
    }

}
