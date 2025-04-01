package com.reservemate.reserve_mate_backend.match.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.hibernate.annotations.SQLDelete;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;

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
@Builder
@Getter
@Table(name = "matches")
@SQLDelete(sql = "UPDATE matches SET deleted = true WHERE match_id = ?")
public class Match extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id", nullable = false, updatable = false)
    private Long matchId;   // 매치 일련번호

    @Column(nullable = false)
    private String matchName;   // 매치명

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus matchStatus; // 매치 상태

    @Column(nullable = false)
    private int teamCapacity; // 최대 참가자 수

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 매치 설명

    @Column(nullable = false)
    private LocalDate matchDate; // 매치 날짜

    @Column(nullable = false)
    private int matchTime; // 매치 시간

    @Column(nullable = false)
    private int endTime; // 매치 종료 시간

    @Column(nullable = false)
    private int matchPrice; // 참가비

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_manager_id")
    private FacilityManager facilityManager;

    @ManyToOne(fetch = FetchType.LAZY) // lazy 지연로딩, eager 즉시 로딩 toOne은 지연로딩 사용
    @JoinColumn(name = "court_id")
    private Court court;

    public Match(int start, int end) {
        this.matchTime = start;
        this.endTime = end;
    }

    // 매치 시간 겹치는지 검증
    public static void isTimeConfilict(List<Match> matches, int startTime, int endTime) {
        if (!matches.isEmpty()) {
            LocalTime newStartTime = LocalTime.of(startTime, 00);
            LocalTime newEndTime = LocalTime.of(endTime, 0);

            for (Match match : matches) {
                LocalTime existStarTime = LocalTime.of(match.getMatchTime(), 0);
                LocalTime existEndTime = LocalTime.of(match.getEndTime(), 0);

                if (Utils.isTimeConflict(existStarTime, existEndTime, newStartTime, newEndTime)) {
                    throw new ApiException(ErrorCode.EXIST_MATCH_TIME_ERROR);
                }
            }
        }
    }

    public void isOverMatch() { // 날짜가 지난 매치인지 검증
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalDateTime matchDateTime = LocalDateTime.of(this.matchDate, LocalTime.of(this.matchTime, 0));

        if (nowDateTime.isAfter(matchDateTime)) {
            throw new ApiException(ErrorCode.END_MATCH_ERROR);
        }
    }

    public void chgMatchStatus(int playerCnt) {
        int teamCapacityHalf = (this.teamCapacity / 2);

        if (this.matchStatus == MatchStatus.APPLICABLE) { // 참가자가 반이 넘은 경우
            if (playerCnt >= teamCapacityHalf) {
                this.matchStatus = MatchStatus.CLOSE_TO_DEADLINE;
            }
        }

        if (this.matchStatus == MatchStatus.CLOSE_TO_DEADLINE) {  // 참가자가 다 찬 경우
            if (playerCnt == this.teamCapacity) {
                this.matchStatus = MatchStatus.FINISH;
            }

            if (playerCnt < teamCapacityHalf) {
                this.matchStatus = MatchStatus.APPLICABLE;
            }
        }

        if (this.matchStatus == MatchStatus.FINISH) { // 인원이 마감된 매치에 매치를 이탈한 인원이 있는 경우
            this.matchStatus = MatchStatus.CLOSE_TO_DEADLINE;
        }
    }

    public void chgFinish() {
        this.matchStatus = MatchStatus.FINISH;
    }

    public void isFinish() { // 종료된 매치인지 검사
        if (this.matchStatus == MatchStatus.FINISH) {
            throw new IllegalArgumentException("이미 종료된 매치입니다.");
        }
    }

    public Facility getFacility() { // 시설 엔티티 가져오기
        return this.getCourt().getFacility();
    }

    public LocalDateTime getFullMatchDateTime() {
        LocalTime matchTime = LocalTime.parse(this.matchTime + ":00:00", DateTimeFormatter.ofPattern("HH:mm:ss"));
        LocalDateTime matchDateTime = LocalDateTime.of(this.matchDate, matchTime);
        return Utils.localDateTimeFormat(matchDateTime);
    }

    // 매치 정보 수정
    public void modifyMatch(int teamCapacity, String description) {
        this.teamCapacity = teamCapacity;
        this.description = description;
    }

}
