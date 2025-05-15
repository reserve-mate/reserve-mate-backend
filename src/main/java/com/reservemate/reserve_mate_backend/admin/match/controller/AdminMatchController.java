package com.reservemate.reserve_mate_backend.admin.match.controller;

import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.admin.match.dto.request.AdminMatchesRequest;
import com.reservemate.reserve_mate_backend.admin.match.dto.response.AdminMatchDetailResponse;
import com.reservemate.reserve_mate_backend.admin.match.dto.response.AdminMatchesResponse;
import com.reservemate.reserve_mate_backend.admin.match.service.AdminMatchService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/admin/match")
@RequiredArgsConstructor
public class AdminMatchController {

    private final AdminMatchService adminMatchService;

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
    public ResponseEntity<Slice<AdminMatchesResponse>> getMatches(HttpServletRequest request,
        @RequestBody AdminMatchesRequest adminMatchesRequest) {
        return ResponseEntity.ok(adminMatchService.getMatches(request, adminMatchesRequest));
    }

}
