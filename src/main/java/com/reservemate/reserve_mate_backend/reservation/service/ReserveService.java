package com.reservemate.reserve_mate_backend.reservation.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.reservation.domain.ReservationStatus;
import com.reservemate.reserve_mate_backend.reservation.repository.ReserveRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReserveService {

    private final ReserveRepository reserveRepository;
    private final MatchRepository matchRepository;
    private final CourtRepository courtRepository;
    private final OperationHourRepository operationHourRepository;

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
