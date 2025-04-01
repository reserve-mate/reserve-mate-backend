package com.reservemate.reserve_mate_backend.match.domain;

public enum MatchStatus {
    FINISH  // 마감
    , CLOSE_TO_DEADLINE // 마감 임박
    , APPLICABLE    // 모집 중
    , END // 매치 종료
}
