package com.reservemate.reserve_mate_backend.reservation.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
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
import com.reservemate.reserve_mate_backend.reservation.repository.ReserveRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
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
    private final JwtUtil jwtUtil;

    /* 예약 목록 조회 */
    public Slice<ReservationsResponse> getReservations(HttpServletRequest request, String type, Integer pageNum) {

        if (!type.equals("upcoming") && !type.equals("past")) {
            throw new ApiException(ErrorCode.INVALID_RESERVATION_SCOPE);
        }

        String accessToken = request.getHeader("access");
        Long userId = jwtUtil.getId(accessToken);

        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<ReservationStatus> status = null;
        if (type.equals("upcoming")) {
            status = List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);
        } else if (type.equals("past")) {
            status = List.of(ReservationStatus.COMPLETED, ReservationStatus.CANCELED);
        }

        Pageable pageable = PageRequest.of(pageNum, 1, Sort.by(
            Sort.Order.desc("reserveDate"), Sort.Order.asc("startTime"), Sort.Order.desc("id")
        )
        );
        Slice<Reservation> reservationSlice = reserveRepository.findByUserAndStatusIn(user, status, pageable);

        return ReservationsResponse.toReservationsSlice(reservationSlice);
    }

    /* 예약 상세 조회 */
    public ReservationDetailResponse getReservationDetail(Long reservationId) {

        Reservation reservation = reserveRepository.findById(reservationId)
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_RESERVATION));

        ReservationDetailResponse response = null;

        if (reservation.getStatus() == ReservationStatus.PENDING) {  // 대기 상태인 경우
            response = ReservationDetailResponse.toReservationDetailPending(reservation);
        } else {
            Payment payment = paymentRepository.findByReservation(reservation)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_PAYMENT));
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

        return OperatingHour.getAvailableHours(operatingHours.get(0), matchTimes, reserveTimes);
    }

}
