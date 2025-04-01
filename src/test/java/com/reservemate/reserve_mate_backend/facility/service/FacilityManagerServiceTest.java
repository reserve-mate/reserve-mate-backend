package com.reservemate.reserve_mate_backend.facility.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityManagerNamesDto;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.domain.UserRole;

import jakarta.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
public class FacilityManagerServiceTest {

    @Mock
    private FacilityManagerRepository facilityManagerRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @InjectMocks
    private FacilityManagerService facilityManagerService;

    @Test
    @DisplayName("매니져 이름 목록 조회")
    @Transactional
    void testGetFacilityManagers() {
        /* given */
        Facility facility = getFacility();
        List<FacilityManager> managers = getFacilityManagers(facility);

        given(facilityRepository.findById(facility.getId())).willReturn(Optional.of(facility));
        given(facilityManagerRepository.findByFacility(facility)).willReturn(managers);

        /* when */
        List<FacilityManagerNamesDto> namesDtos = facilityManagerService.getFacilityManagers(facility.getId());

        /* then */
        assertThat(namesDtos.size()).isEqualTo(2);
        assertThat(namesDtos.get(0).getManagerName()).isEqualTo(managers.get(0).getUser().getName());
    }

    private List<FacilityManager> getFacilityManagers(Facility facility) {
        List<FacilityManager> managers = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            FacilityManager manager = FacilityManager.builder()
                .facility(facility)
                .user(getUser(i))
                .build();

            managers.add(manager);
        }

        return managers;
    }

    private User getUser(int idx) {
        User user = User.builder()
            .id(Long.valueOf(idx))
            .name("이름" + idx)
            .email("email" + idx + "@email.com")
            .password("password")
            .phone("01000000000")
            .role(UserRole.ROLE_USER)
            .build();

        return user;
    }

    private Facility getFacility() {
        Address address = Address.builder()
            .city("서울")
            .build();

        Facility facility = Facility.builder()
            .id(Long.valueOf(1))
            .name("시설" + 1)
            .address(address)
            .sportType(SportType.FUTSAL)
            .conventient("1000")
            .build();
        return facility;
    }
}
