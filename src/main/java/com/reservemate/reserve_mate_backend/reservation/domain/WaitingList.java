package com.reservemate.reserve_mate_backend.reservation.domain;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.user.domain.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "waiting_lists")
@SQLDelete(sql = "UPDATE waiting_lists SET deleted = true WHERE waiting_list_id = ?")
public class WaitingList extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waiting_list_id", updatable = false)
    private Long id;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WaitingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @Builder
    public WaitingList(
            LocalDate date, LocalTime startTime, LocalTime endTime, User user, Court court) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = WaitingStatus.WAITING;
        this.user = user;
        this.court = court;
    }

    public void notified() {
        this.status = WaitingStatus.NOTIFIED;
    }

    public void reserved() {
        this.status = WaitingStatus.RESERVED;
    }

    public void cancel() {
        this.status = WaitingStatus.CANCELED;
    }

    public void expire() {
        this.status = WaitingStatus.EXPIRED;
    }
}
