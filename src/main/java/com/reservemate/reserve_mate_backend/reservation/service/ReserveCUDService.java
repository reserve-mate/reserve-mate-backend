package com.reservemate.reserve_mate_backend.reservation.service;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.reservation.dto.request.CreateReservation;
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
    private final JwtUtil jwtUtil;

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
