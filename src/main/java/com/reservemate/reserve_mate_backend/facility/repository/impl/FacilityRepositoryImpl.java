package com.reservemate.reserve_mate_backend.facility.repository.impl;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.reservemate.reserve_mate_backend.facility.domain.QCourt;
import com.reservemate.reserve_mate_backend.facility.domain.QFacility;
import com.reservemate.reserve_mate_backend.facility.domain.QFacilityImage;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.facility.dto.request.RequestFacilitySearchDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityDto;
import com.reservemate.reserve_mate_backend.facility.repository.CustomFacilityRepository;
import com.reservemate.reserve_mate_backend.reservation.domain.QReservation;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.StringUtils;

public class FacilityRepositoryImpl implements CustomFacilityRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public FacilityRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Slice<FacilityDto> findAllByCursor(RequestFacilitySearchDto requestFacilitySearchDto, Pageable pageable) {
        QFacility facility = QFacility.facility;
        QCourt court = QCourt.court;
        QReservation reservation = QReservation.reservation;
        QFacilityImage image = QFacilityImage.facilityImage;

        List<FacilityDto> results = jpaQueryFactory
            .select(Projections.constructor(FacilityDto.class,
                facility.id,
                facility.name,
                facility.sportType.stringValue(),
                facility.address.city
                    .concat(" ")
                    .concat(facility.address.district.stringValue())
                    .concat(" ")
                    .concat(facility.address.streetAddress.stringValue())
                    .concat(" ")
                    .concat(facility.address.detailAddress.stringValue())
                    .as("address"),
                JPAExpressions
                    .select(court.countDistinct())
                    .from(court)
                    .where(court.facility.eq(facility)),
                JPAExpressions
                    .select(reservation.countDistinct())
                    .from(reservation)
                    .join(reservation.court, court)
                    .where(court.facility.eq(facility)),
//                court.id.countDistinct(),
//                reservation.id.countDistinct()
                JPAExpressions
                    .select(image.imageUrl)
                    .from(image)
                    .where(
                        image.facility.eq(facility),
                        image.main.isTrue()
                    )
                    .limit(1)
            ))
            .from(facility)
//            .leftJoin(court).on(court.facility.eq(facility))
//            .leftJoin(reservation).on(reservation.court.eq(court))
            .where(
                eqLastId(requestFacilitySearchDto.getLastId()),
                keywordContains(requestFacilitySearchDto.getKeyword()),
                eqSportType(requestFacilitySearchDto.getSportType()),
                existsCourtFeeBetween(facility, requestFacilitySearchDto.getMinPrice(), requestFacilitySearchDto
                    .getMaxPrice())
            )
//            .groupBy(facility.id)
            .orderBy(facility.id.desc())
            .limit(requestFacilitySearchDto.getSize() + 1)  //불러올 글 갯수보다 1개 더 가져온다.
            .fetch();

//        System.out.println(requestFacilitySearchDto.getSize());
        boolean hasNext = results.size() > pageable.getPageSize();

        if (hasNext) {
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }

    private BooleanExpression eqLastId(Long lastId) {
        return lastId != null ? QFacility.facility.id.lt(lastId) : null;
    }

    private BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        return QFacility.facility.name.containsIgnoreCase(keyword)
            .or(QFacility.facility.sportType.stringValue().containsIgnoreCase(keyword))
            .or(QFacility.facility.address.city.concat(" ")
                .concat(QFacility.facility.address.district).concat(" ")
                .concat(QFacility.facility.address.streetAddress).containsIgnoreCase(keyword));
    }

    private BooleanExpression eqSportType(SportType sportType) {
        if (sportType == null) {
            return null;
        }

        return QFacility.facility.sportType.eq(sportType);
    }

    private Predicate existsCourtFeeBetween(QFacility facility, Integer minPrice, Integer maxPrice) {
        QCourt court = QCourt.court;

        return JPAExpressions
            .selectOne()
            .from(court)
            .where(
                court.facility.eq(facility),
                court.fee.goe(minPrice),
                court.fee.loe(maxPrice)
            )
            .exists();
    }

}
