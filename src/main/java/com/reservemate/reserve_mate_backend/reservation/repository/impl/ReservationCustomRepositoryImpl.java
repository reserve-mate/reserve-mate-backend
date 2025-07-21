package com.reservemate.reserve_mate_backend.reservation.repository.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.reservemate.reserve_mate_backend.admin.reservation.dto.response.AdminReservationResponse;
import com.reservemate.reserve_mate_backend.admin.reservation.dto.response.DashboardReservationResponse;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.QCourt;
import com.reservemate.reserve_mate_backend.facility.domain.QFacility;
import com.reservemate.reserve_mate_backend.facility.domain.QFacilityManager;
import com.reservemate.reserve_mate_backend.reservation.domain.QReservation;
import com.reservemate.reserve_mate_backend.reservation.domain.ReservationStatus;
import com.reservemate.reserve_mate_backend.reservation.dto.response.ReservationsResponse;
import com.reservemate.reserve_mate_backend.reservation.repository.ReservationCustomRepository;
import com.reservemate.reserve_mate_backend.review.domain.QReview;
import com.reservemate.reserve_mate_backend.user.domain.QUser;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReservationCustomRepositoryImpl implements ReservationCustomRepository {

    private final JPAQueryFactory queryFactory;

    QReservation reservation = QReservation.reservation;
    QCourt court = QCourt.court;
    QFacility facility = QFacility.facility;
    QFacilityManager facilityManager = QFacilityManager.facilityManager;
    QUser user = QUser.user;

    /* 예약 내역 (past) */
    @Override
    public Slice<ReservationsResponse> findByUserAndStatusIn(Long userId, List<ReservationStatus> status,
        Pageable pageable) {

        QReview review = QReview.review;

        List<ReservationsResponse> responses = queryFactory.select(
            Projections.fields(ReservationsResponse.class,
                reservation.id.as("reservationId"), reservation.status.as("reservationStatus"), facility.id.as(
                    "facilityId"), facility.name.as("facilityName"), court.name.as("courtName"), facility.sportType.as(
                        "sportType"), facilityFullAddress().as("address"), reservation.reserveDate.as(
                            "reservationDate"), reservation.startTime.as("startTime"), reservation.endTime.as(
                                "endTime"), review.id.as("reviewId")
            )
        ).from(reservation)
            .join(court).on(reservation.court.id.eq(court.id))
            .join(facility).on(facility.id.eq(court.facility.id))
            .leftJoin(review).on(reservation.id.eq(review.reservation.id))
            .where(reservation.user.id.eq(userId), reservation.status.in(status))
            .orderBy(reservation.reserveDate.desc(), reservation.startTime.asc(), reservation.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return checkEndPage(pageable, responses);
    }

    /* 관리자 예약 현황 */
    @Override
    public Slice<AdminReservationResponse> getAdminReservations(Long userId, String searchTerm,
        ReservationStatus reservationStatus, Long facilityId, LocalDate searchDate, Pageable pageable) {

        List<AdminReservationResponse> responses = queryFactory
            .select(
                Projections.fields(AdminReservationResponse.class, reservation.id.as("reservationId"),
                    reservation.user.name.as("userName"), facility.name.as("facilityName"), reservation.court.name.as(
                        "courtName"), reservation.reserveDate.as("reservationDate"), reservation.startTime.as(
                            "startTime"), reservation.endTime.as("endTime"), reservation.status.as("reservationStatus"),
                    reservation.totalPrice.as("totalPrice"))
            )
            .from(reservation)
            .join(reservation.court, court)
            .join(facility).on(facility.id.eq(court.facility.id))
            .join(facilityManager).on(facilityManager.facility.id.eq(facility.id), facilityManager.user.id.eq(userId))
            .where(whereSearchTerm(searchTerm), whereReservationStatus(reservationStatus), whereReservationDate(
                searchDate), whereFacility(facilityId))
            .orderBy(reservation.reserveDate.desc(), reservation.startTime.asc(), reservation.id.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return checkEndPage(pageable, responses);
    }

    private StringExpression facilityFullAddress() {
        return Expressions.stringTemplate("concat_ws(' ', {0}, {1}, {2}, {3})", facility.address.city,
            facility.address.district, facility.address.streetAddress, facility.address.detailAddress);
    }

    // 무한스크롤
    private <T> Slice<T> checkEndPage(Pageable pageable, List<T> list) {
        boolean hasNext = false;
        if (list.size() > pageable.getPageSize()) {
            hasNext = true;
            list.remove(pageable.getPageSize()); // 한개 더 가져왔으니 더 가져온 데이터 삭제
        }

        return new SliceImpl<>(list, pageable, hasNext);
    }

    // 시설 조건
    private BooleanExpression whereFacility(Long facilityId) {
        return (facilityId != 0) ? facility.id.eq(facilityId) : null;
    }

    // 날짜 조건
    private BooleanExpression whereReservationDate(LocalDate reservationDate) {
        return (reservationDate != null) ? reservation.reserveDate.eq(reservationDate) : null;
    }

    // 예약 상태
    private BooleanExpression whereReservationStatus(ReservationStatus status) {
        return (status != null) ? reservation.status.eq(status) : null;
    }

    // 검색어 조건(고객명, 시설명)
    private BooleanExpression whereSearchTerm(String searchTerm) {
        return (searchTerm != null) ? (reservation.user.name.startsWith(searchTerm).or(facility.name.startsWith(
            searchTerm))) : null;
    }

    // 해당 월 조건
    private BooleanExpression betweenMonth(Integer year, Integer month) {
        int lastMonthDay = Utils.getLastMonthDay(year, month);
        return reservation.reserveDate.between(LocalDate.of(year, month, 1), LocalDate.of(year, month, lastMonthDay));
    }

    /* 관리자 대시보드 최근 예약 조회 */
    @Override
    public List<DashboardReservationResponse> getDashboardReservationResponse(Long userId, Long facilityId,
        Integer year, Integer month) {

        List<DashboardReservationResponse> responses = queryFactory
            .select(Projections.fields(DashboardReservationResponse.class, reservation.id.as("reservationId"),
                reservation.user.name.as("userName"), facility.name.as("facilityName"), reservation.reserveDate.as(
                    "reserveDate"), reservation.startTime.as("startTime"), reservation.endTime.as("endTime"),
                reservation.status.as("reservationStatus"), reservation.totalPrice.as("totalPrice")))
            .from(reservation)
            .join(court).on(reservation.court.id.eq(court.id))
            .join(facility).on(court.facility.id.eq(facility.id))
            .join(facilityManager).on(facilityManager.facility.id.eq(facility.id), facilityManager.user.id.eq(userId))
            .where(whereFacility(facilityId), betweenMonth(year, month))
            .orderBy(reservation.id.desc())
            .limit(4L)
            .fetch();

        return responses;
    }

}
