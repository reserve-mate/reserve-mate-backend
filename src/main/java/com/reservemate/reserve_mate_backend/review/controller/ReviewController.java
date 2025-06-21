package com.reservemate.reserve_mate_backend.review.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.reservemate.reserve_mate_backend.common.auth.service.CustomUserDetails;
import com.reservemate.reserve_mate_backend.review.dto.request.ReviewModifyRequest;
import com.reservemate.reserve_mate_backend.review.dto.request.ReviewRequestDto;
import com.reservemate.reserve_mate_backend.review.dto.response.ReviewCountResponse;
import com.reservemate.reserve_mate_backend.review.dto.response.ReviewListResponse;
import com.reservemate.reserve_mate_backend.review.service.ReviewCUDService;
import com.reservemate.reserve_mate_backend.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewCUDService reviewCUDService;
    private final ReviewService reviewService;

    @GetMapping("/{facilityId}/reviewInfo")
    public ResponseEntity<ReviewCountResponse> getReviewInfo(@PathVariable("facilityId") Long facilityId) {
        return ResponseEntity.ok(reviewService.getFacilityReviewCnt(facilityId));
    }

    /* 리뷰 목록 조회 */
    @GetMapping("/{facilityId}/reviews")
    public ResponseEntity<Slice<ReviewListResponse>> getFacilityReviews(@PathVariable("facilityId") Long facilityId,
        @RequestParam("pageNum") Integer pageNum, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Long userId = null;

        if (customUserDetails != null) { // 로그인 한 유저인 경우
            userId = customUserDetails.getId();
        }

        return ResponseEntity.ok(reviewService.getFacilityReviews(facilityId, pageNum, userId));
    }

    /* 리뷰 삭제 */
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable("reviewId") Long reviewId) {
        reviewCUDService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }

    /* 리뷰 수정 */
    @PutMapping("/modify/{id}")
    public ResponseEntity<Void> modifyReview(@PathVariable("reviewId") Long reviewId,
        @RequestPart("modifyRequest") ReviewModifyRequest modifyRequest,
        @RequestPart(name = "files", required = false) List<MultipartFile> files) {
        reviewCUDService.modifyReview(modifyRequest, files);
        return ResponseEntity.ok().build();
    }

    /* 리뷰 작성 */
    @PostMapping("/registReview")
    public ResponseEntity<Void> registReview(@AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestPart("reviewRequest") ReviewRequestDto reviewRequest,
        @RequestPart(name = "files", required = false) List<MultipartFile> files) {
        reviewCUDService.createReview(customUserDetails.getId(), reviewRequest, files);

        return ResponseEntity.ok().build();
    }

}
