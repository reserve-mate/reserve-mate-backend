package com.reservemate.reserve_mate_backend.facility.repository.impl;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.reservemate.reserve_mate_backend.facility.domain.QCourt;
import com.reservemate.reserve_mate_backend.facility.domain.QFacility;
import com.reservemate.reserve_mate_backend.facility.domain.QFacilityImage;
import com.reservemate.reserve_mate_backend.facility.domain.QFacilityManager;
import com.reservemate.reserve_mate_backend.facility.domain.QOperatingHour;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.facility.dto.request.RequestFacilitySearchDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseCourtDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseFacilitiesDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseFacilityDetailDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseOperatingHourDto;
import com.reservemate.reserve_mate_backend.facility.dto.response.ResponseReviewFacilityDto;
import com.reservemate.reserve_mate_backend.facility.repository.CustomFacilityRepository;
import com.reservemate.reserve_mate_backend.reservation.domain.QReservation;
import com.reservemate.reserve_mate_backend.review.domain.QReview;
import jakarta.persistence.EntityNotFoundException;
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

    public Slice<ResponseFacilitiesDto> findAllCourtsByCursor(RequestFacilitySearchDto facilitySearchDto,
        Pageable pageable) {
        QFacility facility = QFacility.facility;
        QCourt court = QCourt.court;
        QReservation reservation = QReservation.reservation;
        QFacilityImage image = QFacilityImage.facilityImage;

        List<ResponseFacilitiesDto> results = jpaQueryFactory
            .select(Projections.constructor(ResponseFacilitiesDto.class,
                facility.id,
                facility.name,
                facility.sportType.stringValue(),
                facility.address.city.concat(" ")
                    .concat(facility.address.district.stringValue())
                    .concat(" ").concat(facility.address.streetAddress.stringValue())
                    .concat(" ").concat(facility.address.detailAddress.stringValue())
                    .as("address"),
                court.id,
                court.name,
                court.fee,
                JPAExpressions
                    .select(image.imageUrl)
                    .from(image)
                    .where(image.facility.eq(facility), image.main.isTrue())
                    .limit(1)
            ))
            .from(court)
            .join(court.facility, facility)
            .where(
                eqLastCourtId(facilitySearchDto.getLastId()),
                keywordContains(facilitySearchDto.getKeyword()),
                eqSportType(facilitySearchDto.getSportType()),
                court.fee.goe(facilitySearchDto.getMinPrice() != null ? facilitySearchDto.getMinPrice() : 0),
                court.fee.loe(facilitySearchDto.getMaxPrice() != null ? facilitySearchDto.getMaxPrice() : 999_999_999)
            )
            .orderBy(court.id.desc())
            .limit(facilitySearchDto.getSize() + 1)
            .fetch();

        boolean hasNext = results.size() > pageable.getPageSize();

        if (hasNext) {
            results.remove(pageable.getPageSize());
        }
        return new SliceImpl<>(results, pageable, hasNext);
    }

    private BooleanExpression eqLastCourtId(Long lastId) {
        return lastId != null ? QCourt.court.id.lt(lastId) : null;
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
                courtFeeFilterNeeded(requestFacilitySearchDto)
                    ? existsCourtFeeBetween(facility, requestFacilitySearchDto.getMinPrice(), requestFacilitySearchDto
                        .getMaxPrice())
                    : null  //없는 경우 조건 미포함 처리
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

    private boolean courtFeeFilterNeeded(RequestFacilitySearchDto requestFacilitySearchDto) {
        return requestFacilitySearchDto.getMinPrice() != null || requestFacilitySearchDto.getMaxPrice() != null;
    }

    public ResponseFacilityDetailDto findFacilityDetailByCourtId(Long courtId) {
        QCourt court = QCourt.court;
        QFacility facility = QFacility.facility;
        QOperatingHour operatingHour = QOperatingHour.operatingHour;
        QFacilityManager manager = QFacilityManager.facilityManager;
        QFacilityImage image = QFacilityImage.facilityImage;
        QReview review = QReview.review;

        ResponseFacilityDetailDto result = jpaQueryFactory
            .select(Projections.fields(ResponseFacilityDetailDto.class,
                facility.id.as("facilityId"),
                facility.name.as("facilityName"),
                facility.sportType.as("sportType"),
                facility.address.city.concat(" ").concat(facility.address.district)
                    .concat(" ").concat(facility.address.streetAddress)
                    .concat(" ").concat(facility.address.detailAddress).as("address"),
                facility.description.as("description"),
                manager.user.phone.as("managerPhoneNumber"),
                image.imageUrl.as("imageUrl"),
                review.rating.avg().coalesce(0.0).as("rating")
            ))
            .from(court)
            .join(court.facility, facility)
            //.leftJoin(operatingHour).on(operatingHour.facility.eq(facility))
            .leftJoin(manager).on(manager.facility.eq(facility))
            .leftJoin(image).on(image.facility.eq(facility))
            .leftJoin(review).on(review.facility.eq(facility))
            .where(court.id.eq(courtId))
            .groupBy(
                facility.id, manager.user.phone, image.imageUrl
            )
            .fetchOne();

        if (result == null) {
            throw new EntityNotFoundException("해당 코트에 대한 시설을 찾을 수 없습니다.");
        }

        Long facilityId = result.getFacilityId();

        List<ResponseOperatingHourDto> hours = jpaQueryFactory.select(
            Projections.fields(ResponseOperatingHourDto.class,
                operatingHour.dayOfWeek.as("dayOfWeek"),
                operatingHour.openTime.as("openTime"),
                operatingHour.closeTime.as("closeTime"),
                operatingHour.holiday.as("holiday")
            )
        ).from(operatingHour)
            .where(operatingHour.facility.id.eq(facilityId))
            .fetch();

        List<ResponseCourtDto> courts = jpaQueryFactory
            .select(Projections.constructor(ResponseCourtDto.class,
                court.id,
                court.name,
                court.courtType,
                court.width,
                court.height,
                court.indoor,
                court.active,
                court.fee
            ))
            .from(court)
            .where(court.facility.id.eq(facilityId))
            .fetch();

        List<ResponseReviewFacilityDto> reviews = jpaQueryFactory
            .select(Projections.constructor(ResponseReviewFacilityDto.class,
                review.id,
                review.rating,
                review.title,
                review.content
            ))
            .from(review)
            .where(review.facility.id.eq(facilityId))
            .limit(2)
            .fetch();

        return ResponseFacilityDetailDto.builder()
            .facilityId(facilityId)
            .facilityName(result.getFacilityName())
            .sportType(result.getSportType())
            .address(result.getAddress())
            .hours(hours)
            .courts(courts)
            .reviews(reviews)
            .managerPhoneNumber(result.getManagerPhoneNumber())
            .imageUrl(result.getImageUrl())
            .rating(result.getRating())
            .build();
    }
}
