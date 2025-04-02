package com.reservemate.reserve_mate_backend.match.repository.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.reservemate.reserve_mate_backend.match.domain.QMatch;
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
            .groupBy(match.matchDate)
            .fetch();

        return matchDates;
    }

    @Override
    public List<MatchesDto> getMatches(MatchSearchDto matchSearchDto) {
        // TODO Auto-generated method stub
        return null;
    }

}
