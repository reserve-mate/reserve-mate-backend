package com.reservemate.reserve_mate_backend.payment.domain;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import com.reservemate.reserve_mate_backend.reserve.domain.Reservation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Table(name = "payments")
public class Payment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payment_id", updatable = false)
  private Long id;

  @Column(name = "imp_uid", nullable = false)
  private String impUid;

  @Column(name = "merchant_uid", nullable = false)
  private String merchantUid;

  @Column(name = "amount", nullable = false)
  private int amount;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "pay_method", nullable = false)
  private String payMethod;

  @Column(name = "paid_at", nullable = false)
  private LocalDateTime paidAt;

  @Column(name = "canceled_at")
  private LocalDateTime canceledAt;

  @Column(name = "cancel_reason")
  private String cancelReason;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reserve_id")
  private Reservation reservation;
}
