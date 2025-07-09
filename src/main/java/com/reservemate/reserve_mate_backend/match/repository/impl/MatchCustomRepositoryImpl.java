package com.reservemate.reserve_mate_backend.match.repository.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
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
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchHistroyResponse;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchesDto;
import com.reservemate.reserve_mate_backend.match.repository.MatchCustomRepository;
import com.reservemate.reserve_mate_backend.review.domain.QReview;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MatchCustomRepositoryImpl implements MatchCustomRepository {

    private final JPAQueryFactory query;

    private QMatch match = QMatch.match;
    private QMatchPlayer matchPlayer = QMatchPlayer.matchPlayer;
    private QCourt court = QCourt.court;
    private QFacility facility = QFacility.facility;

    /* 완료한 매치 이력 조회 */
    @Override
    public Slice<MatchHistroyResponse> getMatchHistory(Long userId, List<PlayerStatus> playerStatus,
        Pageable pageable) {

        QReview review = QReview.review;

        List<MatchHistroyResponse> response = query.select(
            Projections.fields(MatchHistroyResponse.class,
                match.matchId.as("matchId"), matchPlayer.playerId.as("playerId"), match.matchName.as("matchName"),
                facility.sportType.as("sportType"), matchPlayer.status.as("playerStatus"), match.matchStatus.as(
                    "matchStatus"), facility.id.as("facilityId"), facility.name.as("facilityName"),
                facility.address.city
                    .concat(" ")
                    .concat(facility.address.district.stringValue())
                    .concat(" ")
                    .concat(facility.address.streetAddress.stringValue()).as("address"), match.matchDate.as(
                        "matchDate"), match.matchTime.as("matchTime"), match.endTime.as("endTime"), match.matchPrice.as(
                            "matchPrice"), match.teamCapacity.as("teamCapacity"), ExpressionUtils.as(getPlayerCnt(
                                playerStatus), "playerCnt"), review.id.max().as("reviewId"), matchPlayer.removalReason
                                    .as("ejectReason")
            )
        ).from(matchPlayer)
            .join(matchPlayer.match, match)
            .join(match.court, court)
            .join(court.facility, facility)
            .leftJoin(review).on(review.match.matchId.eq(match.matchId))
            .where(matchPlayer.user.id.eq(userId), wherePlayerStatus(matchPlayer, playerStatus))
            .groupBy(matchPlayer.match.matchId, match.matchName, matchPlayer.playerId, matchPlayer.status,
                match.matchStatus, facility.id, facility.name, match.teamCapacity
            )
            .orderBy(matchPlayer.match.matchDate.desc(), matchPlayer.match.matchTime.asc(), matchPlayer.match.matchId
                .desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return checkEndPage(pageable, response);
    }

    /* 참가자 상태 where */
    private BooleanExpression wherePlayerStatus(QMatchPlayer matchPlayer, List<PlayerStatus> playerStatus) {
        return (playerStatus != null) ? matchPlayer.status.in(playerStatus) : null;
    }

    /* 매치 이력 플레이어 카운트 서브쿼리 */
    private Expression<Long> getPlayerCnt(List<PlayerStatus> playerStatus) {
        if (playerStatus == null)
            playerStatus = List.of(PlayerStatus.COMPLETED, PlayerStatus.ONGOING,
                PlayerStatus.KICKED, PlayerStatus.READY);
        QMatchPlayer subPlayer = QMatchPlayer.matchPlayer;
        Expression<Long> playerCnt = JPAExpressions.select(subPlayer.count())
            .from(subPlayer)
            .where(subPlayer.match.matchId.eq(match.matchId), wherePlayerStatus(subPlayer, playerStatus));
        return playerCnt;
    }

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
            .join(match.court, court)
            .join(court.facility, facility)
            .where(
                betweenTwoWeek(matchSearchDto.getMatchDate()), sportTypeEq(matchSearchDto.getSportType()),
                searchValueLike(matchSearchDto.getSearchValue()), match.matchStatus.ne(MatchStatus.CANCELLED),
                matchStatusEqUser(matchSearchDto.getMatchStatus()), cityEq(matchSearchDto.getRegion())
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
        return (sportType != null) ? facility.sportType.eq(sportType) : null;
    }

    // 검색어로 조회
    private BooleanExpression searchValueLike(String searchValue) {
        String likeSearch = "%" + searchValue + "%";
        return (searchValue != null) ? match.matchName.like(likeSearch).or(
            facility.name.like(likeSearch))
            : null;
    }

    // 매치 상태 조회
    private BooleanExpression matchStatusEq(MatchStatus matchStatus) {
        return (matchStatus != null) ? match.matchStatus.eq(matchStatus) : null;
    }

    private BooleanExpression matchStatusEqUser(MatchStatus matchStatus) {
        return (matchStatus != null) ? match.matchStatus.eq(matchStatus) : match.matchStatus.ne(MatchStatus.CANCELLED);
    }

    // 지역 검색
    private BooleanExpression cityEq(String city) {
        return (city.equals("대전/세종")) ? facility.address.city.eq("대전").or(facility.address.city.eq("세종"))
            : facility.address.city.eq(city);
    }

    /* 사용자입장 매치 목록 조회 */
    @Override
    public Slice<MatchesDto> getMatches(Pageable pageable, MatchSearchDto matchSearchDto) {

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
            .join(match.court, court)
            .join(court.facility, facility)
            .leftJoin(matchPlayer).on(
                matchPlayer.match.eq(match), matchPlayer.status.in(playerStatus)
            )
            .where(
                matchDateEq(matchSearchDto.getMatchDate()), sportTypeEq(matchSearchDto.getSportType()), searchValueLike(
                    matchSearchDto.getSearchValue()), matchStatusEqUser(
                        matchSearchDto.getMatchStatus()), cityEq(matchSearchDto.getRegion())
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

        List<PlayerStatus> playerStatus = List.of(PlayerStatus.READY, PlayerStatus.ONGOING, PlayerStatus.KICKED,
            PlayerStatus.COMPLETED);

        List<AdminMatchesResponse> matchesResponses = query.select(
            Projections.fields(AdminMatchesResponse.class,
                match.matchId.as("matchId"), match.matchName.as("matchName"), match.matchDate.as(("matchDate")),
                match.matchTime.as("matchTime"), match.endTime.as("endTime"), facility.sportType.as("sportType"),
                facility.name.as("facilityName"), match.teamCapacity.as("teamCapacity"), matchPlayer.count().as(
                    "playerCnt"), match.matchStatus.as("matchStatus")
            )
        )
            .from(facilityManager)
            .join(facilityManager.facility, facility)
            .join(court).on(court.facility.id.eq(facilityManager.facility.id))
            .join(match).on(match.courtId.eq(court.id))
            .leftJoin(matchPlayer).on(
                matchPlayer.match.matchId.eq(match.matchId), matchPlayer.status.in(playerStatus)
            )
            .where(
                facilityManager.user.id.eq(userId), searchValueLike(adminMatchesRequest.getSearchValue()), sportTypeEq(
                    adminMatchesRequest.getSportType()), betweenDate(adminMatchesRequest.getStartDate(),
                        adminMatchesRequest.getEndDate()), matchStatusEq(adminMatchesRequest.getMatchStatus())
            )
            .groupBy(match.matchDate, match.matchTime, match.matchId)
            .offset(pageable.getOffset())
            .orderBy(match.matchDate.desc(), match.matchTime.desc(), match.matchId.desc())
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return checkEndPage(pageable, matchesResponses);
    }

}
