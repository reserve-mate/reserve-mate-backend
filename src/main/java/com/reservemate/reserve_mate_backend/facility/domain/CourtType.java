package com.reservemate.reserve_mate_backend.facility.domain;

public enum CourtType {

    // 테니스
    CLAY_TENNIS, HARD, GRASS, SYNTHETIC_TENNIS,

    // 풋살
    RUBBER_FUTSAL,   // 고무바닥
    SYNTHETIC_FUTSAL, // 합성 표면
    ARTIFICIAL_TURF_FUTSAL, // 인조잔디

    // 농구
    WOODEN_BASKET,  // 목재 바닥
    SYNTHETIC_BASKET, // 합성 표면

    // 배구
    WOODEN_VOLLEY,  // 목재 바닥
    SYNTHETIC_VOLLEY, // 합성표면

    // 배드민턴
    WOODEN_BM,  // 목재 바닥
    SYNTHETIC_BM, // 합성표면
    RUBBER_BM, // 고무바닥

    // 야구
    NATURE_GRASS_BASE,  // 천연잔디
    ARTIFICIAL_TURF_BASE, // 인조잔디
    DIRT_BASE, // 흙 바닥
    CLAY_BASE, // 점토 바닥

    // 축구
    NATURE_GRASS_FB,    // 천연잔디
    ARTIFICIAL_TURF_FB, // 인조잔디
    DIRT_FB // 흙 바닥

}
