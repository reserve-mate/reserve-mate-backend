package com.reservemate.reserve_mate_backend.review.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.common.file.service.FileService;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.reservation.validator.Validator;
import com.reservemate.reserve_mate_backend.review.domain.Review;
import com.reservemate.reserve_mate_backend.review.domain.ReviewImage;
import com.reservemate.reserve_mate_backend.review.dto.request.ReviewRequestDto;
import com.reservemate.reserve_mate_backend.review.repository.ReviewImageRepository;
import com.reservemate.reserve_mate_backend.review.repository.ReviewRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewCUDService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final Validator validator;
    private final FileService fileService;

    @Value("${spring.app.file.review}")
    private String reviewImagePath;

    private final static int MAX_FILE_COUNT = 3;

    /* 리뷰 등록 */
    @Transactional
    public void createReview(Long userId, ReviewRequestDto reviewRequestDto, List<MultipartFile> files) {

        if (files.size() > MAX_FILE_COUNT) {
            throw new ApiException(ErrorCode.MAX_FILE_COUNT3);
        }

        boolean isExistReview = reviewRepository.existsByReservationNumber(reviewRequestDto.getReservationNumber());
        if (isExistReview) {
            throw new ApiException(ErrorCode.EXIST_RESERVATION_REVIEW);
        }

        Reservation reservation = validator.reservationCompleteChk(reviewRequestDto.getReservationNumber(),
            reviewRequestDto.getCourtId(), userId);

        Review review = reviewRequestDto.toEntity(reservation);
        Review saveReview = reviewRepository.save(review);

        if (files == null || files.isEmpty()) {
            return;
        }

        List<String> imagePaths = fileService.uploadFiles(files, reviewImagePath);
        List<ReviewImage> reviewImages = IntStream.range(0, imagePaths.size())
            .mapToObj(i -> {
                String path = imagePaths.get(i);
                return reviewRequestDto.toReviewImageEntity(path, saveReview, (i + 1));
            }).toList();

        reviewImageRepository.saveAll(reviewImages);
    }

}
