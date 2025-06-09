package com.reservemate.reserve_mate_backend.review.domain;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reviews")
@SQLDelete(sql = "UPDATE reviews SET deleted = true WHERE review_id = ?")
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id", updatable = false)
    private Long id;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "is_visible", nullable = false, columnDefinition = "BOOLEAN DEFAULT true") //공개/비공개
    private Boolean isVisible = true;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImages = new ArrayList<>();

    @Builder
    public Review(Integer rating, String content, User user, Facility facility, Reservation reservation, Boolean isVisible, List<ReviewImage> reviewImages) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
        this.content = content;
        this.user = user;
        this.facility = facility;
        this.reservation = reservation;
        this.isVisible = isVisible != null ? isVisible : true;
        if (reviewImages != null) {
            this.reviewImages = reviewImages;
        }
    }

    public void update(Integer rating, String content, List<ReviewImage> reviewImages) {
        if (rating != null) {
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }
            this.rating = rating;
        }
        if (content != null) {
            this.content = content;
        }
        if (reviewImages != null) {
            this.reviewImages.clear();
            this.reviewImages.addAll(reviewImages);
        }
    }

    public void addReviewImage(ReviewImage reviewImage) {
        this.reviewImages.add(reviewImage);
        reviewImage.setReview(this);
    }

    public void removeReviewImage(ReviewImage reviewImage) {
        this.reviewImages.remove(reviewImage);
        reviewImage.setReview(null);
    }
}
