package com.reservemate.reserve_mate_backend.admin.match.controller;

import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.admin.match.dto.request.AdminMatchesRequest;
import com.reservemate.reserve_mate_backend.admin.match.dto.response.AdminMatchesResponse;
import com.reservemate.reserve_mate_backend.admin.match.service.AdminMatchService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/admin/match")
@RequiredArgsConstructor
public class AdminMatchController {

    private final AdminMatchService adminMatchService;

    /* 관리자 매치 목록 조회 */
    @PostMapping("/getMatches")
    public ResponseEntity<List<AdminMatchesResponse>> getMatches(HttpServletRequest request,
        @RequestBody AdminMatchesRequest adminMatchesRequest) {
        return ResponseEntity.ok(adminMatchService.getMatches(request, adminMatchesRequest));
    }

}
