package com.reservemate.reserve_mate_backend.match.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class MatchCustomRepositoryTest {

    @Test
    void testGetMatches() {

    }
}
