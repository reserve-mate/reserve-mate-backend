package com.reservemate.reserve_mate_backend.match.domain;

import java.time.LocalDate;

import org.hibernate.annotations.SQLDelete;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import com.reservemate.reserve_mate_backend.facility.domain.Court;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "matches")
@SQLDelete(sql = "UPDATE matches SET deleted = true WHERE match_id = ?")
public class Match extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id", nullable = false)
    private Long matchId;

    @Column(nullable = false)
    private String manager;

    @Column(nullable = false)
    private MatchStatus matchStatus;

    @Column(nullable = false)
    private int teamCapacity;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate matchDate;

    @Column(nullable = false)
    private int matchTime;

    @Column(nullable = false)
    private int matchPrice;

    @ManyToOne(fetch = FetchType.LAZY) // lazy 지연로딩, eager 즉시 로딩 toOne은 지연로딩 사용
    @JoinColumn(name = "court_id")
    private Court court;

}
