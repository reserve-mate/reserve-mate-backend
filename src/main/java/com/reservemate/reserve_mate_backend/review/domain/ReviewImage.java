package com.reservemate.reserve_mate_backend.review.domain;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review_images")
public class ReviewImage extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_image_id", updatable = false)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "image_order")
    private Integer imageOrder = 1;

    @Builder
    public ReviewImage(String imageUrl, Review review, Integer imageOrder) {
        this.imageUrl = imageUrl;
        this.review = review;
        this.imageOrder = (imageOrder != null) ? imageOrder : 1;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setReview(Review review) {
        this.review = review;
    }
}