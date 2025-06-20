package com.reservemate.reserve_mate_backend.review.repository.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.reservemate.reserve_mate_backend.review.domain.QReview;
import com.reservemate.reserve_mate_backend.review.domain.QReviewImage;
import com.reservemate.reserve_mate_backend.review.dto.response.ReviewCountResponse;
import com.reservemate.reserve_mate_backend.review.dto.response.ReviewListResponse;
import com.reservemate.reserve_mate_backend.review.dto.response.ReviewListResponse.ReviewImageResponse;
import com.reservemate.reserve_mate_backend.review.repository.ReviewCustomRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

    private final JPAQueryFactory queryFactory;

    private QReview review = QReview.review;
    private QReviewImage reviewImage = QReviewImage.reviewImage;

    /* 해당 시설의 리뷰 정보 조회 */
    @Override
    public ReviewCountResponse getReviewInfo(Long facilityId) {

        ReviewCountResponse response = queryFactory.select(
            Projections.fields(ReviewCountResponse.class, review.facility.name.as("facilityName"), review.facility.id
                .count().as("reviewCnt"), review.rating.avg().coalesce(0.0).as("rating"))
        ).from(review)
            .where(review.facility.id.eq(facilityId))
            .fetchOne();

        return response;
    }

    /* 리뷰 목록 조회 */
    @Override
    public Slice<ReviewListResponse> getReviewListResponses(Long facilityId, Pageable pageable) {

        List<Long> reviewIds = pagingIds(facilityId, pageable);

        boolean hasNext = reviewIds.size() > pageable.getPageSize();
        if (hasNext) {
            reviewIds.remove(pageable.getPageSize());
        }

        List<Tuple> tuples = queryFactory.select(
            review.id, review.user.id, review.user.name, review.rating, review.createdAt, review.title, review.content,
            reviewImage.imageUrl, reviewImage.imageOrder
        ).from(review)
            .leftJoin(reviewImage).on(review.id.eq(reviewImage.review.id))
            .where(review.id.in(reviewIds))
            .orderBy(review.id.desc())
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
                    .reviewTitle(tuple.get(review.title))
                    .reviewContent(tuple.get(review.content))
                    .reviewImages(new ArrayList<>())
                    .build();
                return response;
            });

            String imageUrl = tuple.get(reviewImage.imageUrl);
            Integer imageOrder = tuple.get(reviewImage.imageOrder);
            if (imageUrl != null && imageOrder != null) {
                listResponse.getReviewImages().add(new ReviewImageResponse(imageUrl, imageOrder));
            }

        }

        List<ReviewListResponse> responses = new ArrayList<>(responseMap.values());
        responses.forEach(response -> { // responseMap을 순회하면서 각 리뷰 이미지 정렬 
            response.getReviewImages().sort(Comparator.comparingInt(ReviewImageResponse::getImageOrder));
        });

        return new SliceImpl<>(responses, pageable, hasNext); //checkEndPage(pageable, responses, reviewIds);
    }

    // 리뷰 ID만 가져오기(페이징 전용)
    private List<Long> pagingIds(Long facilityId, Pageable pageable) {
        List<Long> reviewIds = queryFactory.select(review.id)
            .from(review)
            .where(review.facility.id.eq(facilityId))
            .orderBy(review.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return reviewIds;
    }

}
