package com.reservemate.reserve_mate_backend.match.domain;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import com.reservemate.reserve_mate_backend.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Getter
@Builder
@Table(name = "matchplayers")
@SQLDelete(sql = "UPDATE matchplayers SET deleted = true WHERE player_id = ?")
@SQLRestriction("deleted = false")
public class MatchPlayer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id", updatable = false, nullable = false)
    private Long playerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PlayerStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    public void isCanCancel() {  // 취소 가능 여부 검사
        if (this.status == PlayerStatus.CANCEL) {
            throw new IllegalArgumentException("이미 취소된 매치입니다.");
        } else if (this.status == PlayerStatus.COMPLETED) {
            throw new IllegalArgumentException("이미 참여했던 이력이 있는 매치입니다.");
        }
    }

    // 매치 삭제로 인한 플레이서 상태 변경
    public void chgMatchRemoved() {
        this.status = PlayerStatus.MATCH_REMOVED;
    }

    // 매치 신청 상태 취소로 수정
    public void chgStatusCancel() {
        this.status = PlayerStatus.CANCEL;
    }

}
