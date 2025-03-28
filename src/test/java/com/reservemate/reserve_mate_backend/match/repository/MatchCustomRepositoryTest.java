package com.reservemate.reserve_mate_backend.match.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

import com.reservemate.reserve_mate_backend.TestConfig;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(TestConfig.class)
public class MatchCustomRepositoryTest {

    @Test
    void testGetMatches() {
        // This test is currently empty but should pass now with proper configuration
    }
}
