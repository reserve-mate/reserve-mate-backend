package com.reservemate.reserve_mate_backend.review.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.common.file.service.FileService;
import com.reservemate.reserve_mate_backend.common.file.validator.FileValidator;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.validator.MatchValidator;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.reservation.validator.Validator;
import com.reservemate.reserve_mate_backend.review.domain.Review;
import com.reservemate.reserve_mate_backend.review.domain.ReviewImage;
import com.reservemate.reserve_mate_backend.review.domain.ReviewType;
import com.reservemate.reserve_mate_backend.review.dto.request.ReviewModifyRequest;
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
    private final FileService fileService;

    private final Validator validator;
    private final MatchValidator matchValidator;

    @Value("${spring.app.file.review}")
    private String reviewImagePath;

    private final static int MAX_FILE_COUNT = 3;

    /* 리뷰 삭제 */
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ApiException(
            ErrorCode.NOT_FOUND_REVIEW));

        /* 해당 리뷰와 관련된 이미지 목록 */
        List<ReviewImage> reviewImages = reviewImageRepository.findByReviewOrderByImageOrderAsc(review);
        if (!reviewImages.isEmpty()) {
            reviewImageRepository.deleteAll(reviewImages);
        }

        reviewRepository.delete(review);
    }

    /* 리뷰 수정 */
    @Transactional
    public void modifyReview(Long reviewId, ReviewModifyRequest modifyRequest, List<MultipartFile> files) {

        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ApiException(
            ErrorCode.NOT_FOUND_REVIEW));
        review.update(modifyRequest.getRating(), modifyRequest.getTitle(), modifyRequest.getContent());

        List<ReviewImage> existingImages = reviewImageRepository.findByReviewOrderByImageOrderAsc(review);

        List<Integer> delOrderIds = modifyRequest.getDelOrderIds();
        if (!delOrderIds.isEmpty()) { // 삭제된 파일이 있는 경우
            reviewImageRepository.deleteReviewImage(modifyRequest.getDelOrderIds(), reviewId);
            // 조건에 맞는 요소 제거
            existingImages.removeIf(image -> delOrderIds.contains(image.getImageOrder()));
        }

        if (files == null || files.isEmpty()) {
            return;
        }

        int remainImageCnt = existingImages.size();
        int newFileCnt = files.size();
        int total = remainImageCnt + newFileCnt;

        if (total > MAX_FILE_COUNT) {
            throw new ApiException(ErrorCode.MAX_FILE_COUNT3);
        }

        FileValidator.validatorFiles(files); // 파일 확장자 검사

        List<String> imagePaths = fileService.uploadFiles(files, reviewImagePath);
        List<ReviewImage> newImages = IntStream.range(0, imagePaths.size())
            .mapToObj(i -> {
                String path = imagePaths.get(i);
                int newOrderIds = remainImageCnt + i;
                return new ReviewImage(path, review, newOrderIds);
            }).toList();

        reviewImageRepository.saveAll(newImages);
    }

    public void createMatchReview() {

    }

    /* 예약 리뷰 등록 */
    @Transactional
    public void createReview(Long userId, ReviewRequestDto reviewRequestDto, List<MultipartFile> files) {

        Review review = null;
        if (reviewRequestDto.getReviewType() == ReviewType.RESERVATION) {
            Reservation reservation = validator.reservationCompleteChk(reviewRequestDto.getRentId(), reviewRequestDto
                .getCourtId(), userId);
            review = reviewRequestDto.toEntity(reservation);
        } else if (reviewRequestDto.getReviewType() == ReviewType.MATCH) {
            MatchPlayer matchPlayer = matchValidator.getMatchCompleteChk(reviewRequestDto.getRentId(), userId);
            review = reviewRequestDto.toEntity(matchPlayer);
        }

        Review saveReview = reviewRepository.save(review);

        if (files == null || files.isEmpty()) {
            return;
        }

        if (files.size() > MAX_FILE_COUNT) {
            throw new ApiException(ErrorCode.MAX_FILE_COUNT3);
        }

        FileValidator.validatorFiles(files);    // 파일 확장자 검사

        List<String> imagePaths = fileService.uploadFiles(files, reviewImagePath);
        List<ReviewImage> reviewImages = IntStream.range(0, imagePaths.size())
            .mapToObj(i -> {
                String path = imagePaths.get(i);
                return new ReviewImage(path, saveReview, (i + 1));
            }).toList();

        reviewImageRepository.saveAll(reviewImages);
    }

}
