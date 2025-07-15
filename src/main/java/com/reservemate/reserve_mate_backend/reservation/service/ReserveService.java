package com.reservemate.reserve_mate_backend.reservation.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.OperatingHour;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.OperationHourRepository;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentRepository;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.reservation.domain.ReservationStatus;
import com.reservemate.reserve_mate_backend.reservation.dto.response.ReservationDetailResponse;
import com.reservemate.reserve_mate_backend.reservation.dto.response.ReservationsResponse;
import com.reservemate.reserve_mate_backend.reservation.repository.ReservationCustomRepository;
import com.reservemate.reserve_mate_backend.reservation.repository.ReserveRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReserveService {

    private final ReserveRepository reserveRepository;
    private final MatchRepository matchRepository;
    private final CourtRepository courtRepository;
    private final OperationHourRepository operationHourRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ReservationCustomRepository reservationCustomRepository;

    /* 예약 가능 여부 */
    public boolean verifyReservation(Long reservationId) {
        Reservation reservation = reserveRepository.findById(reservationId).orElseThrow(() -> new ApiException(
            ErrorCode.NOT_FOUND_RESERVATION));
        reservation.isNotPending();

        return reserveRepository.existsReservationDate(reservation.getReserveDate(), reservation.getStartTime(),
            reservation.getEndTime(), reservation.getCourtId(), ReservationStatus.CONFIRMED);
    }

    /* 예약 목록 조회 */
    public Slice<ReservationsResponse> getReservations(Long userId, String type, Integer pageNum) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(pageNum, 6, Sort.by(
            Sort.Order.desc("reserveDate"), Sort.Order.asc("startTime"), Sort.Order.desc("id")
        )
        );

        Slice<ReservationsResponse> response = null;
        List<ReservationStatus> status = List.of();

        switch (type) {
            case "upcoming":
                status = List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);
                Slice<Reservation> reservationSlice = reserveRepository.findByUserAndStatusIn(user, status, pageable);
                response = ReservationsResponse.toReservationsSlice(reservationSlice);
                break;

            case "past":
                status = List.of(ReservationStatus.COMPLETED, ReservationStatus.CANCELED);
                response = reservationCustomRepository.findByUserAndStatusIn(userId, status, pageable);
                break;

            default:
                throw new ApiException(ErrorCode.INVALID_RESERVATION_SCOPE);
        }

        return response;
    }

    /* 예약 상세 조회 */
    public ReservationDetailResponse getReservationDetail(Long reservationId) {

        Reservation reservation = reserveRepository.findById(reservationId)
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_RESERVATION));

        ReservationDetailResponse response = null;

        if (reservation.getStatus() == ReservationStatus.PENDING) {  // 대기 상태인 경우
            response = ReservationDetailResponse.toReservationDetailPending(reservation);
        } else if (reservation.getStatus() == ReservationStatus.CANCELED) {
            Optional<Payment> payment = paymentRepository.findByReservation(reservation);
            response = ReservationDetailResponse.toReservationCancelResponse(reservation, payment);
        } else {
            Payment payment = paymentRepository.findByReservation(reservation).orElseThrow(() -> new ApiException(
                ErrorCode.NOT_FOUND_RESERVATION));
            response = ReservationDetailResponse.toReservationDetailResponse(reservation, payment);
        }

        return response;
    }

    /* 예약 가능한 시간 조회 */
    public List<LocalTime> getAvailableTimeSlots(Long courtId, LocalDate reserveDate) {

        Court court = courtRepository.findById(courtId).orElseThrow(() -> new ApiException(
            ErrorCode.INVALID_INPUT_VALUE));

        List<MatchStatus> matchStatus = List.of(MatchStatus.END, MatchStatus.CANCELLED);
        List<Match> matches = matchRepository.findByMatchDateAndCourtAndMatchStatusNotIn(reserveDate, court,
            matchStatus);
        // 매치가 사용중인 시간대 List
        List<LocalTime> matchTimes = Match.getUnavailableHours(matches);

        List<Reservation> reservations = reserveRepository.findByReserveDateAndStatus(reserveDate,
            ReservationStatus.CONFIRMED);
        List<LocalTime> reserveTimes = Reservation.getUnavailableHours(reservations);

        List<Long> facilityIds = List.of(court.getFacilityId());
        List<OperatingHour> operatingHours = operationHourRepository.findByFacilityInAndDayOfWeek(facilityIds,
            reserveDate.getDayOfWeek());
        if (operatingHours.isEmpty()) {
            throw new ApiException(ErrorCode.NO_AVAILABLE_TIME_ON_DAY);
        }

        return OperatingHour.getAvailableHours(reserveDate, operatingHours.get(0), matchTimes, reserveTimes);
    }

}
