package com.reservemate.reserve_mate_backend.admin.match.controller;

import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.admin.match.dto.request.AdminMatchModifyRequest;
import com.reservemate.reserve_mate_backend.admin.match.dto.request.AdminMatchesRequest;
import com.reservemate.reserve_mate_backend.admin.match.dto.response.AdminMatchDetailResponse;
import com.reservemate.reserve_mate_backend.admin.match.dto.response.AdminMatchesResponse;
import com.reservemate.reserve_mate_backend.admin.match.service.AdminMatchService;
import com.reservemate.reserve_mate_backend.common.auth.service.CustomUserDetails;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/admin/match")
@RequiredArgsConstructor
public class AdminMatchController {

    private final AdminMatchService adminMatchService;

    @PutMapping("/edit/{matchId}")
    public ResponseEntity<Void> postMethodName(@PathVariable("matchId") Long matchId,
        @RequestBody AdminMatchModifyRequest modifyRequest) {
        adminMatchService.adminMatchModify(matchId, modifyRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/status/{matchId}")
    public ResponseEntity<Void> updateMatchStat(@PathVariable("matchId") Long matchId,
        @RequestParam("status") MatchStatus status) {
        adminMatchService.matchStatusChange(matchId, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/delete/{matchId}")
    public ResponseEntity<Void> adminDeleteMatch(@PathVariable("matchId") Long matchId) {
        adminMatchService.deleteMatch(matchId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<AdminMatchDetailResponse> getMethodName(@PathVariable("matchId") Long matchId) {
        return ResponseEntity.ok(adminMatchService.getAdminMatchDetail(matchId));
    }

    /* 관리자 매치 목록 조회 */
    @PostMapping("/getMatches")
    public ResponseEntity<Slice<AdminMatchesResponse>> getMatches(
        @AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestBody AdminMatchesRequest adminMatchesRequest) {
        return ResponseEntity.ok(adminMatchService.getMatches(customUserDetails.getId(), adminMatchesRequest));
    }

}
