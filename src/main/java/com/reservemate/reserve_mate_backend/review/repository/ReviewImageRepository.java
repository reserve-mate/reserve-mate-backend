package com.reservemate.reserve_mate_backend.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reservemate.reserve_mate_backend.review.domain.Review;
import com.reservemate.reserve_mate_backend.review.domain.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    /* 해당 리뷰와 관련된 이미지 삭제 */
    @Modifying
    @Query("delete from ReviewImage ri where ri.review.id = :reviewId and ri.imageOrder in (:delOrderIds)")
    void deleteReviewImage(@Param("delOrderIds") List<Integer> delOrderIds, @Param("reviewId") Long reviewId);

    /* 해당 리뷰와 관련된 이미지 개수 */
    int countByReview(Review review);

    /* 해당 리뷰와 관련된 이미지 목록(이미지 번호 오름차순) */
    List<ReviewImage> findByReviewOrderByImageOrderAsc(Review review);

}
