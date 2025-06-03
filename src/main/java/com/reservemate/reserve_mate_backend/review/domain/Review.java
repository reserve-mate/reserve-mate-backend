package com.reservemate.reserve_mate_backend.review.domain;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

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

    @Column(name = "is_visible", nullable = false, columnDefinition = "BOOLEAN DEFAULT true") //공개/비공개
    private Boolean isVisible = true;

    @Column(name = "del_yn", nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    private String delYn = "N";

    @Column(name = "review_image")
    private String reviewImage;


    @Builder
    public Review(Integer rating, String content, User user, Facility facility, Boolean isVisible, String reviewImage, String delYn) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
        this.content = content;
        this.user = user;
        this.facility = facility;
        this.reviewImage = reviewImage;
        this.delYn = delYn != null ? "N" : "Y"; // If reviewImage is provided, set delYn to 'Y'
        this.isVisible = isVisible != null ? isVisible : true;
    }

    public void update(Integer rating, String content) {
        if (rating != null) {
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("Rating must be between 1 and 5");
            }
            this.rating = rating;
        }
        if (content != null) {
            this.content = content;
        }
    }
}
