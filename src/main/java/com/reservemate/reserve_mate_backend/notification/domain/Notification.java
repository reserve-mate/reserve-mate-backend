package com.reservemate.reserve_mate_backend.notification.domain;

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "notification_id", updatable = false)
  private Long id;              
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private Long userId;          // 회원 번호
  
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reservation_id")
  private Long reservationId;   // 예약 번호
  
  @Column(name = "type", nullable = false, length = 3)
  private String type;          // 알림유형 (예약, 결제)
  
  @Column(name = "content", nullable = false, length = 4000)
  private String content;       // 알림내용
  
  @Column(name = "method", nullable = false, length = 3)
  private String method;        // 알림발송방법 (EMAIL, SMS, PUSH)
  
  @Column(name = "read_yn", length = 1)
  private String readYn;        // 읽음여부
  
  @CreatedDate
  @Column(name = "sent_at", updatable = false)
  private LocalDateTime sentAt; // 보낸시간
  
  @Column(name = "read_at")
  private LocalDateTime readAt; // 읽은시간
  
}
