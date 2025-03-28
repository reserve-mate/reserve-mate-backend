package com.reservemate.reserve_mate_backend.match.repository.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.reservemate.reserve_mate_backend.match.domain.QMatch;
import com.reservemate.reserve_mate_backend.match.dto.request.MatchSearchDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchesDto;
import com.reservemate.reserve_mate_backend.match.repository.MatchCustomRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MatchCustomRepositoryImpl implements MatchCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    private QMatch match = QMatch.match;

    @Override
    public List<MatchesDto> getMatches(MatchSearchDto matchSearchDto) {
        // TODO Auto-generated method stub
        return null;
    }

}
