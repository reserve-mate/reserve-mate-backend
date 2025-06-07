package com.reservemate.reserve_mate_backend.match.domain;

import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
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
    @Column(name = "removal_reason")
    private EjectionReason removalReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PlayerStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    public static MatchPlayer toMatchPlayer(User user, Match match) {
        MatchPlayer matchPlayer = MatchPlayer.builder()
            .user(user)
            .match(match)
            .status(PlayerStatus.READY)
            .build();
        return matchPlayer;
    }

    /* 해당 매치가 종료된 매치인지 검증 */
    public void isFinish() {
        this.match.isFinish();
    }

    public void isCanCancel() {  // 취소 가능 여부 검사
        if (this.status == PlayerStatus.CANCEL) {
            throw new ApiException(ErrorCode.ALREADY_CANCEL_PLAYER);
        } else if (this.status == PlayerStatus.COMPLETED) {
            throw new ApiException(ErrorCode.ALREADY_COMPLETE_PLAYER);
        } else if (this.status == PlayerStatus.ONGOING) {
            throw new ApiException(ErrorCode.ALREADY_ONGOING_PLAYER);
        }
    }

    // 매치 삭제로 인한 플레이서 상태 변경
    public void chgMatchRemoved() {
        this.status = PlayerStatus.MATCH_CANCELLED;
    }

    // 매치 신청 상태 취소로 수정
    public void chgStatusCancel() {
        this.status = PlayerStatus.CANCEL;
    }

    // 현재 진행중인 참가자인지 검증
    public void isOngoingPlayer() {
        if (this.status == PlayerStatus.ONGOING) {
            throw new ApiException(ErrorCode.ALREADY_ONGOING_PLAYER);
        }
    }

    public void isNotOngoingPlayer() {
        if (this.status != PlayerStatus.ONGOING) {
            throw new ApiException(ErrorCode.NOT_ONGOING_PLAYER);
        }
    }

    // 참가자 강퇴 상태 변경
    public void removePlayer(EjectionReason ejectionReason) {
        this.removalReason = ejectionReason;
        this.status = PlayerStatus.KICKED;
    }

    // 매치 아이디 조회
    public static List<Match> getMatches(List<MatchPlayer> matchPlayers) {
        return matchPlayers.stream()
            .map(matchPlayer -> matchPlayer.getMatch())
            .distinct().toList();
    }

}
