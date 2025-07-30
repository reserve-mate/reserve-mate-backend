package com.reservemate.reserve_mate_backend.review.validator;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.reservemate.reserve_mate_backend.review.dto.response.PopularityReviewResponse;
import com.reservemate.reserve_mate_backend.review.repository.ReviewCustomRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewValidator {

    private final ReviewCustomRepository reviewCustomRepository;

    // 가장 높은 평점의 리뷰 가져오기
    public List<Long> getHighRate() {

        List<PopularityReviewResponse> reviews = reviewCustomRepository.getHighRate();

        return reviews.stream()
            .map(PopularityReviewResponse::getFacilityId)
            .collect(Collectors.toList());
    }

}
