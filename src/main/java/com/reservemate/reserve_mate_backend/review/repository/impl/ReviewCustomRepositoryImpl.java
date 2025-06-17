package com.reservemate.reserve_mate_backend.review.repository.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.reservemate.reserve_mate_backend.review.domain.QReview;
import com.reservemate.reserve_mate_backend.review.domain.QReviewImage;
import com.reservemate.reserve_mate_backend.review.dto.response.ReviewListResponse;
import com.reservemate.reserve_mate_backend.review.repository.ReviewCustomRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory queryFactory;

    private QReview review = QReview.review;
    private QReviewImage reviewImage = QReviewImage.reviewImage;

    /* 리뷰 목록 조회 */
    @Override
    public List<ReviewListResponse> getReviewListResponses(Long facilityId, Long userId) {

        List<Tuple> tuples = queryFactory.select(
            review.id, review.user.id, review.user.name, review.rating, review.createdAt, review.content,
            reviewImage.imageUrl
        ).from(review)
            .leftJoin(reviewImage).on(review.id.eq(reviewImage.review.id))
            .where(review.facility.id.eq(facilityId))
            .fetch();

        Map<Long, ReviewListResponse> responseMap = new LinkedHashMap<>();

        for (Tuple tuple : tuples) {
            Long reviewId = tuple.get(review.id);

            ReviewListResponse listResponse = responseMap.computeIfAbsent(reviewId, id -> {
                ReviewListResponse response = ReviewListResponse.builder()
                    .reviewId(id)
                    .userId(tuple.get(review.user.id))
                    .userName(tuple.get(review.user.name))
                    .rating(tuple.get(review.rating))
                    .reviewDate(tuple.get(review.createdAt))
                    //.reviewTitle(tuple.get(review.ti))
                    .reviewContent(tuple.get(review.content))
                    .reviewImage(new ArrayList<>())
                    .build();
                return response;
            });

            String imageUrl = tuple.get(reviewImage.imageUrl);
            if (imageUrl != null) {
                listResponse.getReviewImage().add(imageUrl);
            }
        }

        return new ArrayList<>(responseMap.values());
    }

}
