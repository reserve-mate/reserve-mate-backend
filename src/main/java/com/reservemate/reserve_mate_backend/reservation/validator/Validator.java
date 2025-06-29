package com.reservemate.reserve_mate_backend.reservation.validator;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.OperatingHour;
import com.reservemate.reserve_mate_backend.facility.repository.OperationHourRepository;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.reservation.domain.ReservationStatus;
import com.reservemate.reserve_mate_backend.reservation.dto.request.CreateReservation;
import com.reservemate.reserve_mate_backend.reservation.repository.ReserveRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Validator {

    private final ReserveRepository reserveRepository;
    private final MatchRepository matchRepository;
    private final OperationHourRepository operationHourRepository;

    // 코트에 의한 예약 목록 조회
    public List<Reservation> getCourtsReservations(List<Court> courts, Integer year, Integer month, Long facilityId) {
        List<Long> courtIds = Court.getCourtIds(courts);
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, Utils.getLastMonthDay(year, month));

        if (facilityId == 0L) {
            return reserveRepository.findByCourtIdsReservationDate(courtIds, startDate, endDate);
        } else {
            return reserveRepository.findByCourtIdsReservationDateFacilityId(courtIds, startDate, endDate, facilityId);
        }

    }

    // 해당 예약이 완료된 예약인지 체크
    public Reservation reservationCompleteChk(Long reservationId, Long courtId, Long userId) {
        Reservation reservation = reserveRepository.findByReservationIdAndCourtIdAndUserId(reservationId, courtId,
            userId).orElseThrow(
                () -> new ApiException(ErrorCode.NOT_FOUND_RESERVATION));
        reservation.isNotComplete();
        return reservation;
    }

    public Reservation reservationCancelChk(String reservationNumber) {
        Reservation reservation = reserveRepository.findByReservationNumber(reservationNumber).orElseThrow(
            () -> new ApiException(ErrorCode.NOT_FOUND_RESERVATION));
        reservation.isNotCancel();
        return reservation;
    }

    /* 예약 확정 시 데이터 검증 */
    public Reservation reservationConfirmValid(Long reservationId) {

        Reservation reservation = reserveRepository.findById(reservationId).orElseThrow(() -> new ApiException(
            ErrorCode.NOT_FOUND_RESERVATION));
        reservation.isNotPending();

        // 해당 시간대에 겹치는 예약이 있는지 조회(비관적 락)
        List<Reservation> reservations = reserveRepository.findOverlappingWithLock(reservation.getCourtId(), reservation
            .getReserveDate(), reservation.getStartTime(), reservation.getEndTime());

        boolean hasConflict = reservations.stream().anyMatch(r -> !r.getId().equals(reservationId) && r.getStatus()
            == ReservationStatus.CONFIRMED);
        if (hasConflict) {
            throw new ApiException(ErrorCode.ALREADY_RESERVED);
        }

        return reservation;
    }

    /* 예약 생성 시 데이터 검증 */
    public void createReservationValid(CreateReservation createReservation, User user, Court court) {
        createReservation.validateNotPast();    // 예약 시간이 현재 시간보다 과거인지 검증
        List<MatchStatus> matchStatus = List.of(MatchStatus.END, MatchStatus.CANCELLED);
        boolean existMatch = matchRepository.existsMatchReaserveDate(createReservation.getReserveDate(),
            createReservation.getStartTime().getHour(), createReservation.getEndTime().getHour(), matchStatus,
            createReservation.getCourtId());  // 예약 시간이 매치 시간과 겹치는지 검증
        if (existMatch) {    // 해당 시간대에 매치가 존재하면 예외 처리
            throw new ApiException(ErrorCode.EXIST_MATCH_TIME_ERROR);
        }

        boolean existReservation = reserveRepository.existsReservationDate(createReservation.getReserveDate(),
            createReservation.getStartTime(), createReservation.getEndTime(), createReservation.getCourtId(),
            ReservationStatus.CONFIRMED);
        if (existReservation) {  // 해당 시간대에 예약이 존재하면 예외처리
            throw new ApiException(ErrorCode.DUPLICATE_RESERVATION);
        }

        List<ReservationStatus> status = List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);
        boolean dupleReservation = reserveRepository
            .existsByReserveDateAndStartTimeAndEndTimeAndStatusInAndUserAndCourt(createReservation.getReserveDate(),
                createReservation.getStartTime(), createReservation.getEndTime(), status, user, court);
        if (dupleReservation) {
            throw new ApiException(ErrorCode.DUPLICATE_ACTIVE_RESERVATION);
        }

        List<Long> facilityIds = List.of(court.getFacilityId());
        List<OperatingHour> operatingHour = operationHourRepository.findByFacilityInAndDayOfWeek(facilityIds,
            createReservation.getReserveDate().getDayOfWeek());
        if (operatingHour.isEmpty()) {
            throw new ApiException(ErrorCode.NO_AVAILABLE_TIME_ON_DAY);
        }
    }

}
