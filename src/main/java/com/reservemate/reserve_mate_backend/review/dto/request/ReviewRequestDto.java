package com.reservemate.reserve_mate_backend.review.dto.request;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.review.domain.Review;
import com.reservemate.reserve_mate_backend.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ReviewRequestDto {

    private Long courtId;
    private Integer rating;
    private String title;
    private String content;

    // 리뷰 Entity
    public Review toEntity(Court court, User user) {
        return Review.builder()
            .rating(this.rating)
            .title(this.title)
            .content(this.content)
            .facility(court.getFacility())
            .user(user)
            .build();
    }

}
