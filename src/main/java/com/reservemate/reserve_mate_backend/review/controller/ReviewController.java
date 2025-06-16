package com.reservemate.reserve_mate_backend.review.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.reservemate.reserve_mate_backend.common.auth.service.CustomUserDetails;
import com.reservemate.reserve_mate_backend.review.dto.request.ReviewModifyRequest;
import com.reservemate.reserve_mate_backend.review.dto.request.ReviewRequestDto;
import com.reservemate.reserve_mate_backend.review.service.ReviewCUDService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewCUDService reviewCUDService;

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
