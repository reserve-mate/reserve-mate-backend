package com.reservemate.reserve_mate_backend.team.domain;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "teams")
@SQLDelete(sql = "UPDATE teams SET deleted = true WHERE team_id = ?")
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id", updatable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Builder
    public Team(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public void update(String name, String description) {
        this.name = name != null ? name : this.name;
        this.description = description != null ? description : this.description;
    }
}
