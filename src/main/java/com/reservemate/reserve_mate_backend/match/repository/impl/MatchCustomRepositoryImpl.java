package com.reservemate.reserve_mate_backend.match.repository.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.reservemate.reserve_mate_backend.facility.domain.QCourt;
import com.reservemate.reserve_mate_backend.facility.domain.QFacility;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.match.domain.QMatch;
import com.reservemate.reserve_mate_backend.match.domain.QMatchPlayer;
import com.reservemate.reserve_mate_backend.match.dto.request.MatchSearchDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchDateDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchesDto;
import com.reservemate.reserve_mate_backend.match.repository.MatchCustomRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MatchCustomRepositoryImpl implements MatchCustomRepository {

    private final JPAQueryFactory query;

    private QMatch match = QMatch.match;

    /* 날짜별 매치 Count */
    @Override
    public List<MatchDateDto> getMatchesForDate(MatchSearchDto matchSearchDto) {

        List<MatchDateDto> matchDates = query.select(
            Projections.fields(MatchDateDto.class,
                match.matchDate.as("matchDate"), match.count().as("matchCnt")
            )
        )
            .from(match)
            .where(
                betweenTwoWeek(matchSearchDto.getMatchDate()), sportTypeEq(matchSearchDto.getSportType()),
                searchValueLike(matchSearchDto.getSearchValue())
            )
            .groupBy(match.matchDate)
            .fetch();

        return matchDates;
    }

    private BooleanExpression betweenTwoWeek(LocalDate matchDate) {
        LocalDate searchDate = LocalDate.now();
        if (matchDate != null)
            searchDate = matchDate;

        LocalDate twoWeekAgo = searchDate.plusDays(13);

        return match.matchDate.between(searchDate, twoWeekAgo);
    }

    private BooleanExpression matchDateEq(LocalDate matchDate) {
        return (matchDate != null) ? match.matchDate.eq(matchDate) : null;
    }

    private BooleanExpression sportTypeEq(SportType sportType) {
        return (sportType != null) ? match.court.facility.sportType.eq(sportType) : null;
    }

    private BooleanExpression searchValueLike(String searchValue) {
        String likeSearch = "%" + searchValue + "%";
        return (searchValue != null && (searchValue.equals(""))) ? match.matchName.like(likeSearch).or(
            match.court.facility.name.like(likeSearch))
            : null;
    }

    @Override
    public Slice<MatchesDto> getMatches(Pageable pageable, MatchSearchDto matchSearchDto) {

        QMatchPlayer matchPlayer = QMatchPlayer.matchPlayer;
        QCourt court = QCourt.court;
        QFacility facility = QFacility.facility;

        List<MatchesDto> matches = query.select(
            Projections.fields(MatchesDto.class,
                match.matchId.as("matchId"), match.matchName.as("matchName"), match.matchStatus.as("matchStatus"),
                facility.name.as("facilityName"), facility.address.city
                    .concat(" ")
                    .concat(facility.address.district.stringValue())
                    .concat(" ")
                    .concat(facility.address.streetAddress.stringValue()).as("fullAddress"), match.matchDate.as(
                        "matchDate"), match.matchTime.as("matchTime"), match.endTime.as("matchEndTime"),
                facility.sportType.as("sportType"), match.matchPrice.as("matchPrice"), match.teamCapacity.as(
                    "teamCapacity"), matchPlayer.count().as("playerCnt")
            )
        )
            .from(match)
            .join(court).on(court.eq(match.court))
            .join(facility).on(facility.eq(court.facility))
            .leftJoin(matchPlayer).on(matchPlayer.match.eq(match)).fetchJoin()
            .where(
                matchDateEq(matchSearchDto.getMatchDate()), sportTypeEq(matchSearchDto.getSportType()), searchValueLike(
                    matchSearchDto.getSearchValue())
            )
            .groupBy(match.matchId)
            .orderBy(match.matchTime.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return checkEndPage(pageable, matches);
    }

    private Slice<MatchesDto> checkEndPage(Pageable pageable, List<MatchesDto> matches) {
        boolean hasNext = false;

        if (matches.size() > pageable.getPageSize()) {
            hasNext = true;
            matches.remove(pageable.getPageSize()); // 한개 더 가져왔으니 더 가져온 데이터 삭제
        }

        return new SliceImpl<>(matches, pageable, hasNext);
    }

}
