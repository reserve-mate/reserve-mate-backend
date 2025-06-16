package com.reservemate.reserve_mate_backend.facility.reposiotry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

@SpringBootTest
public class FacilityRepositoryTest {

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FacilityManagerRepository facilityManagerRepository;

    @Test
    @DisplayName("시설 등록")
    void saveFacility() {
        // Facility facility = facilityRepository.findById(1L)
        //     .orElseThrow(() -> new ApiException(ErrorCode.ADMIN_FORBIDDEN));

        // User user = userRepository.findById(5L)
        //     .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        // FacilityManager facilityManager = new FacilityManager(facility, user);

        // facilityManagerRepository.save(facilityManager);
    }

}
