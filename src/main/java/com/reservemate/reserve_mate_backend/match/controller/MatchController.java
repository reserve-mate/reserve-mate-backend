package com.reservemate.reserve_mate_backend.match.controller;

import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.match.dto.request.CreateMatchDto;
import com.reservemate.reserve_mate_backend.match.dto.request.MatchSearchDto;
import com.reservemate.reserve_mate_backend.match.dto.request.ModifyMatchDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchDateDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchDetailDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchesDto;
import com.reservemate.reserve_mate_backend.match.service.MatchService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @DeleteMapping("/deleteMatch/{matchId}")
    public void deleteMatch(@PathVariable("matchId") Long matchId, @RequestParam("userId") Long userId) {
        matchService.deleteMatch(matchId, userId);
    }

    @PutMapping("/reReCruit/{matchId}")
    public void reReCruit(@PathVariable("matchId") Long matchId, @RequestParam("userId") Long userId) {
        matchService.reReCruit(matchId, userId);
    }

    @PutMapping("/chgFinish/{matchId}")
    public void chgEndMatch(@PathVariable("matchId") Long matchId, @RequestParam("userId") Long userId) {
        matchService.chgEndMatch(matchId, userId);
    }

    /* 매치 목록 조회 */
    @GetMapping("/matches")
    public ResponseEntity<Slice<MatchesDto>> getMethodName(@RequestBody MatchSearchDto matchSearchDto) {
        return ResponseEntity.ok(matchService.getMatches(matchSearchDto));
    }

    /* 날짜별 매치 조회 */
    @GetMapping("/matcheDates")
    public ResponseEntity<List<MatchDateDto>> getMatchDates(@RequestBody MatchSearchDto matchSearchDto) {
        List<MatchDateDto> matchesDtos = matchService.getMatchDates(matchSearchDto);
        return ResponseEntity.ok(matchesDtos);
    }

    /*매치 단일 조회 */
    @GetMapping("/{matchId}")
    public ResponseEntity<MatchDetailDto> getMatch(@PathVariable(name = "matchId") Long matchId,
        @RequestParam(name = "userId") Long userId) {
        MatchDetailDto detailDto = matchService.getMatch(matchId, userId);
        return ResponseEntity.ok(detailDto);
    }

    /* 매치 정보 수정 */
    @PutMapping("/modifyMatch/{matchId}")
    public void modifyMatch(@PathVariable(name = "matchId") Long matchId, @RequestBody ModifyMatchDto modifyMatchDto) {
        matchService.modifyMatch(matchId, modifyMatchDto);
    }

    /* 매치 등록 */
    @PostMapping("/registMatch")
    public void registMatch(@Valid @RequestBody CreateMatchDto createMatchDto) {
        matchService.registMatch(createMatchDto);
    }

}
