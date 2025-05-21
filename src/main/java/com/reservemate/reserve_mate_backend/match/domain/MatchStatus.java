package com.reservemate.reserve_mate_backend.match.domain;

public enum MatchStatus {
    FINISH  // 마감
    , CLOSE_TO_DEADLINE // 마감 임박
    , APPLICABLE    // 모집 중
    , END // 매치 종료
    , ONGOING // 매치 진행중
    , CANCELLED // 매치 취소됨
}
