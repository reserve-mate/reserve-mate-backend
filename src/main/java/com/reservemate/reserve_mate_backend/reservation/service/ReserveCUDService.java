package com.reservemate.reserve_mate_backend.reservation.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.reservation.domain.ReservationStatus;
import com.reservemate.reserve_mate_backend.reservation.dto.request.ConfirmReservationRequest;
import com.reservemate.reserve_mate_backend.reservation.dto.request.CreateReservation;
import com.reservemate.reserve_mate_backend.reservation.dto.request.ReservationCancelRequest;
import com.reservemate.reserve_mate_backend.reservation.repository.ReserveRepository;
import com.reservemate.reserve_mate_backend.reservation.validator.Validator;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReserveCUDService {

    private final ReserveRepository reserveRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;
    private final Validator validator;
    private final ApplicationEventPublisher eventPublisher;

    private static final int BATCH_TIMES = 1000;

    /* 시간별 예약 */
    @Transactional
    public void chgTimeReservation() {
        LocalTime nowTime = LocalTime.of(Utils.getNowTime(), 0);
        chgCancel(nowTime);
        chgConfirm(nowTime);
    }

    // 확정된 COMPLETE 수정
    public void chgConfirm(LocalTime nowTime) {
        List<Reservation> reservations = reserveRepository.findByReserveDateAndStartTimeAndStatus(LocalDate.now(),
            nowTime, ReservationStatus.CONFIRMED);

        if (!reservations.isEmpty()) {
            for (int i = 0; i < reservations.size(); i += BATCH_TIMES) {
                List<Reservation> batchList = reservations.subList(i, Math.min(i + BATCH_TIMES, reservations.size()));
                confirmBatchProcess(batchList); // 확정된 COMPLETE 수정
            }
        }
    }

    public void chgCancel(LocalTime nowTime) {
        List<Reservation> reservations = reserveRepository.findByReserveDateAndStartTimeAndStatus(LocalDate.now(),
            nowTime, ReservationStatus.PENDING);

        if (!reservations.isEmpty()) {
            for (int i = 0; i < reservations.size(); i += BATCH_TIMES) {
                List<Reservation> batchList = reservations.subList(i, Math.min(i + BATCH_TIMES, reservations.size()));
                cancelBatchProcess(batchList);
            }
        }
    }

    // 해당 시간의 대기중인 예약 취소 처리
    private void cancelBatchProcess(List<Reservation> batchList) {
        for (Reservation reservation : batchList) {
            reservation.cancel("일정 시간 내에 결제가 완료되지 않아 예약이 자동으로 취소되었습니다.");
        }
    }

    // 확정된 COMPLETE 수정
    private void confirmBatchProcess(List<Reservation> batchList) {
        for (Reservation reservation : batchList) {
            reservation.complete();
        }
    }

    /* 예약 취소 */
    @Transactional
    public String reservationCancel(Long reservationId, String cancelReason) {
        Reservation reservation = reserveRepository.findById(reservationId).orElseThrow(() -> new ApiException(
            ErrorCode.NOT_FOUND_RESERVATION));
        reservation.isCompleteOrCancel();

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            eventPublisher.publishEvent(new ReservationCancelRequest(reservationId, cancelReason));
        }
        reservation.cancel(cancelReason);

        return reservation.getReservationNumber();
    }

    /* 예약 확정 */
    @Transactional
    public void reservationConfirm(ConfirmReservationRequest confirmReservationRequest) {
        Reservation reservation = confirmReservationRequest.getReservation();
        reservation.confirm();

        List<Reservation> pendings = reserveRepository.findByOtherReservations(reservation.getReserveDate(), reservation
            .getStartTime(), reservation.getEndTime(), reservation.getCourtId(), ReservationStatus.PENDING);
        if (!pendings.isEmpty()) {
            for (Reservation pending : pendings) {
                pending.cancel("해당 시간은 이미 다른 예약이 잡혀 있습니다.");
            }

            reserveRepository.saveAll(pendings);
        }
    }

    /* 예약(대기) 생성 */
    @Transactional
    public Long createReservation(Long userId, CreateReservation createReservation) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Court court = courtRepository.findById(createReservation.getCourtId()).orElseThrow(() -> new ApiException(
            ErrorCode.INVALID_INPUT_VALUE));

        validator.createReservationValid(createReservation, user, court);    // 예약 생성 시 데이터 검증

        Reservation reservation = createReservation.toEntity(user, court);
        Reservation saveReservation = reserveRepository.save(reservation);
        return saveReservation.getId();
    }

}
