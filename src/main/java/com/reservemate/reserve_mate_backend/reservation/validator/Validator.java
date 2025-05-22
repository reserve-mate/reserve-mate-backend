package com.reservemate.reserve_mate_backend.reservation.validator;

import java.util.List;

import org.springframework.stereotype.Component;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.OperatingHour;
import com.reservemate.reserve_mate_backend.facility.repository.OperationHourRepository;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
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
