package com.reservemate.reserve_mate_backend.match.repository.impl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.reservemate.reserve_mate_backend.admin.match.dto.request.AdminMatchesRequest;
import com.reservemate.reserve_mate_backend.admin.match.dto.response.AdminMatchesResponse;
import com.reservemate.reserve_mate_backend.facility.domain.QCourt;
import com.reservemate.reserve_mate_backend.facility.domain.QFacility;
import com.reservemate.reserve_mate_backend.facility.domain.QFacilityManager;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.match.domain.QMatch;
import com.reservemate.reserve_mate_backend.match.domain.QMatchPlayer;
import com.reservemate.reserve_mate_backend.match.dto.request.MatchSearchDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchDateDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchesDto;
import com.reservemate.reserve_mate_backend.match.repository.MatchCustomRepository;
import com.reservemate.reserve_mate_backend.user.domain.QUser;
import com.reservemate.reserve_mate_backend.user.domain.UserRole;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MatchCustomRepositoryImpl implements MatchCustomRepository {

    private final JPAQueryFactory query;

    private QMatch match = QMatch.match;

    @Override
    public boolean existConfilictMatch(LocalDate matchDate, Long userId, Long courtId, int matchTime, int endTime) {

        QFacilityManager facilityManager = QFacilityManager.facilityManager;

        long result = query.select(match.count())
            .from(match)
            .join(facilityManager)
            .on(match.facilityManager.id.eq(facilityManager.id))
            .where(
                matchDateEq(matchDate), facilityManager.user.id.eq(userId), match.court.id.ne(courtId), match.matchTime
                    .lt(endTime), match.endTime.gt(matchTime)
            )
            .fetchOne();

        return result > 0;
    }

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
                searchValueLike(matchSearchDto.getSearchValue()), match.matchStatus.ne(MatchStatus.CANCELLED),
                matchStatusEq(matchSearchDto.getMatchStatus())
            )
            .groupBy(match.matchDate)
            .fetch();

        return matchDates;
    }

    // 2주간의 날짜 between(해당 날짜로 부터 2주 후)
    private BooleanExpression betweenTwoWeek(LocalDate matchDate) {
        LocalDate searchDate = LocalDate.now();
        if (matchDate != null)
            searchDate = matchDate;

        LocalDate twoWeekAgo = searchDate.plusDays(13);

        return match.matchDate.between(searchDate, twoWeekAgo);
    }

    // 날짜 기간 검색
    private BooleanExpression betweenDate(LocalDate startDate, LocalDate endDate) {
        return (startDate != null && endDate != null) ? match.matchDate.between(startDate, endDate) : null;
    }

    // 날짜로 조회
    private BooleanExpression matchDateEq(LocalDate matchDate) {
        return (matchDate != null) ? match.matchDate.eq(matchDate) : null;
    }

    // sportType 조회
    private BooleanExpression sportTypeEq(SportType sportType) {
        return (sportType != null) ? match.court.facility.sportType.eq(sportType) : null;
    }

    // 검색어로 조회
    private BooleanExpression searchValueLike(String searchValue) {
        String likeSearch = "%" + searchValue + "%";
        return (searchValue != null) ? match.matchName.like(likeSearch).or(
            match.court.facility.name.like(likeSearch))
            : null;
    }

    // 매치 상태 조회
    private BooleanExpression matchStatusEq(MatchStatus matchStatus) {
        return (matchStatus != null) ? match.matchStatus.eq(matchStatus) : null;
    }

    /* 사용자입장 매치 목록 조회 */
    @Override
    public Slice<MatchesDto> getMatches(Pageable pageable, MatchSearchDto matchSearchDto) {

        QMatchPlayer matchPlayer = QMatchPlayer.matchPlayer;
        QCourt court = QCourt.court;
        QFacility facility = QFacility.facility;

        List<PlayerStatus> playerStatus = List.of(PlayerStatus.READY, PlayerStatus.ONGOING, PlayerStatus.KICKED,
            PlayerStatus.COMPLETED);

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
            //.join(facility).on(facility.eq(court.facility))
            .leftJoin(matchPlayer).on(
                matchPlayer.match.eq(match), matchPlayer.status.in(playerStatus)
            )
            .where(
                matchDateEq(matchSearchDto.getMatchDate()), sportTypeEq(matchSearchDto.getSportType()), searchValueLike(
                    matchSearchDto.getSearchValue()), match.matchStatus.ne(MatchStatus.CANCELLED), matchStatusEq(
                        matchSearchDto.getMatchStatus())
            )
            .groupBy(match.matchId)
            .orderBy(match.matchTime.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return checkEndPage(pageable, matches);
    }

    private <T> Slice<T> checkEndPage(Pageable pageable, List<T> matches) {
        boolean hasNext = false;

        if (matches.size() > pageable.getPageSize()) {
            hasNext = true;
            matches.remove(pageable.getPageSize()); // 한개 더 가져왔으니 더 가져온 데이터 삭제
        }

        return new SliceImpl<>(matches, pageable, hasNext);
    }

    /* 관리자 관점 */
    @Override
    public Slice<AdminMatchesResponse> getAdminMatches(Long userId, AdminMatchesRequest adminMatchesRequest,
        Pageable pageable) {

        QFacilityManager facilityManager = QFacilityManager.facilityManager; // 기본 관리자 테이블
        QFacilityManager adminManager = QFacilityManager.facilityManager;   // user아이디와 관련된 facility 매핑 테이블
        QUser user = QUser.user;
        QCourt court = QCourt.court;
        QMatchPlayer matchPlayer = QMatchPlayer.matchPlayer;
        QFacility facility = QFacility.facility;

        List<UserRole> roles = Arrays.asList(UserRole.ROLE_ADMIN, UserRole.ROLE_FACILITY_MANAGER);
        List<PlayerStatus> playerStatus = List.of(PlayerStatus.READY, PlayerStatus.ONGOING, PlayerStatus.KICKED,
            PlayerStatus.COMPLETED);

        List<AdminMatchesResponse> matchesResponses = query.select(
            Projections.fields(AdminMatchesResponse.class,
                match.matchId.as("matchId"), match.matchName.as("matchName"), match.matchDate.as(("matchDate")),
                match.matchTime.as("matchTime"), match.endTime.as("endTime"), facility.sportType.as("sportType"),
                facility.name.as("facilityName"), match.teamCapacity.as("teamCapacity"), matchPlayer.countDistinct().as(
                    "playerCnt"), match.matchStatus.as("matchStatus")
            )
        ).distinct()
            .from(facilityManager)
            .join(adminManager).on(facilityManager.facility.id.eq(adminManager.facility.id))
            .join(facility).on(facility.id.eq(adminManager.facility.id))
            .join(user).on(adminManager.user.id.eq(user.id), user.role.in(roles))
            .join(court).on(facility.id.eq(court.facility.id))
            .join(match).on(match.court.id.eq(court.id))
            .leftJoin(matchPlayer).on(
                matchPlayer.match.matchId.eq(match.matchId), matchPlayer.status.in(playerStatus)
            )
            .where(
                facilityManager.user.id.eq(userId), searchValueLike(adminMatchesRequest.getSearchValue()), sportTypeEq(
                    adminMatchesRequest.getSportType()), betweenDate(adminMatchesRequest.getStartDate(),
                        adminMatchesRequest.getEndDate()), matchStatusEq(adminMatchesRequest.getMatchStatus())
            )
            .groupBy(match.matchId)
            .offset(pageable.getOffset())
            .orderBy(match.matchDate.desc(), match.matchTime.desc(), match.matchId.desc())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return checkEndPage(pageable, matchesResponses);
    }

}
