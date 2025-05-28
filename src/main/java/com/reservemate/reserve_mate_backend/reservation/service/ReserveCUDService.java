package com.reservemate.reserve_mate_backend.reservation.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
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

import jakarta.servlet.http.HttpServletRequest;
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

    private final JwtUtil jwtUtil;

    // 확정된 COMPLETE 수정
    @Transactional
    public void chgConfirm() {
        LocalTime nowTime = LocalTime.of(Utils.getNowTime(), 0);
        List<Reservation> reservations = reserveRepository.findByReserveDateAndStartTimeAndStatus(LocalDate.now(),
            nowTime, ReservationStatus.CONFIRMED);

        if (!reservations.isEmpty()) {
            int batch = 1000;
            for (int i = 0; i < reservations.size(); i += batch) {
                List<Reservation> batchList = reservations.subList(i, Math.min(i + batch, reservations.size()));
                confirmBatchProcess(batchList); // 확정된 COMPLETE 수정
            }
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
    }

    /* 예약(대기) 생성 */
    @Transactional
    public void createReservation(HttpServletRequest request, CreateReservation createReservation) {
        String accessToken = request.getHeader("access");
        if (accessToken == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED_CODE);
        }
        Long userId = jwtUtil.getId(accessToken);

        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Court court = courtRepository.findById(createReservation.getCourtId()).orElseThrow(() -> new ApiException(
            ErrorCode.INVALID_INPUT_VALUE));

        validator.createReservationValid(createReservation, user, court);    // 예약 생성 시 데이터 검증

        Reservation reservation = createReservation.toEntity(user, court);
        reserveRepository.save(reservation);
    }

}
