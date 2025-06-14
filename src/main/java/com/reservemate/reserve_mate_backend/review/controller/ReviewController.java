package com.reservemate.reserve_mate_backend.review.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.reservemate.reserve_mate_backend.common.auth.service.CustomUserDetails;
import com.reservemate.reserve_mate_backend.review.dto.request.ReviewRequestDto;
import com.reservemate.reserve_mate_backend.review.service.ReviewCUDService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewCUDService reviewCUDService;

    @PostMapping("/registReview")
    public ResponseEntity<Void> registReview(@AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestPart("reviewRequest") ReviewRequestDto reviewRequest,
        @RequestPart(name = "files", required = false) List<MultipartFile> files) {
        reviewCUDService.createReview(customUserDetails.getId(), reviewRequest, files);

        return ResponseEntity.ok().build();
    }

}
