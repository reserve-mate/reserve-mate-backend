package com.reservemate.reserve_mate_backend.facility.domain;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "facility_images")
@SQLDelete(sql = "UPDATE facility_images SET deleted = true WHERE facility_image_id = ?")
public class FacilityImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facility_image_id", updatable = false)
    private Long id;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "is_main", nullable = false)
    private boolean main;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Builder
    public FacilityImage(
        String imageUrl,
        String description,
        Boolean main,
        Integer displayOrder,
        Facility facility) {
        this.imageUrl = imageUrl;
        this.description = description;
        this.main = main != null ? main : false;
        this.displayOrder = displayOrder;
        this.uploadedAt = LocalDateTime.now();
        this.facility = facility;
    }

    public void update(String imageUrl, String description, Boolean main, Integer displayOrder) {
        this.imageUrl = imageUrl != null ? imageUrl : this.imageUrl;
        this.description = description != null ? description : this.description;
        this.main = main != null ? main : this.main;
        this.displayOrder = displayOrder != null ? displayOrder : this.displayOrder;
    }

    public void setAsMain() {
        this.main = true;
    }

    public void unsetAsMain() {
        this.main = false;
    }
}
