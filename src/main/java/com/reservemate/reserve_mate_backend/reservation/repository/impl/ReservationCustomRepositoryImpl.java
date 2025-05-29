package com.reservemate.reserve_mate_backend.reservation.repository.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.reservemate.reserve_mate_backend.admin.reservation.dto.response.DashboardReservationResponse;
import com.reservemate.reserve_mate_backend.facility.domain.QCourt;
import com.reservemate.reserve_mate_backend.facility.domain.QFacility;
import com.reservemate.reserve_mate_backend.facility.domain.QFacilityManager;
import com.reservemate.reserve_mate_backend.reservation.domain.QReservation;
import com.reservemate.reserve_mate_backend.reservation.repository.ReservationCustomRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationCustomRepositoryImpl implements ReservationCustomRepository {

    private final JPAQueryFactory queryFactory;

    /* 관리자 대시보드 최근 예약 조회 */
    @Override
    public List<DashboardReservationResponse> getDashboardReservationResponse(Long userId) {

        QReservation reservation = QReservation.reservation;
        QCourt court = QCourt.court;
        QFacility facility = QFacility.facility;
        QFacilityManager facilityManager = QFacilityManager.facilityManager;

        List<DashboardReservationResponse> responses = queryFactory
            .select(Projections.fields(DashboardReservationResponse.class, reservation.id.as("reservationId"),
                reservation.user.name.as("userName"), facility.name.as("facilityName"), reservation.reserveDate.as(
                    "reserveDate"), reservation.startTime.as("startTime"), reservation.endTime.as("endTime"),
                reservation.status.as("reservationStatus"), reservation.totalPrice.as("totalPrice")))
            .from(reservation)
            .join(court).on(reservation.court.id.eq(court.id))
            .join(facility).on(court.facility.id.eq(facility.id))
            .join(facilityManager).on(facilityManager.facility.id.eq(facility.id), facilityManager.user.id.eq(userId))
            .orderBy(reservation.id.desc())
            .limit(4L)
            .fetch();

        return responses;
    }

}
