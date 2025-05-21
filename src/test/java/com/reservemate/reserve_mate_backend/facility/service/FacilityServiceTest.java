package com.reservemate.reserve_mate_backend.facility.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.domain.OperatingHour;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.facility.repository.OperationHourRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.domain.UserRole;

import jakarta.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
public class FacilityServiceTest {

    @Mock
    private FacilityManagerRepository facilityManagerRepository;

    @Mock
    private OperationHourRepository operationHourRepository;

    @InjectMocks
    private FacilityService facilityService;

    private List<OperatingHour> getOperationHours(List<FacilityManager> facilityManagers) {

        List<OperatingHour> hours = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            OperatingHour operatingHour = OperatingHour.builder()
                .id(Long.valueOf(i))
                .dayOfWeek(Utils.getDayOfWeek())
                .openTime(LocalTime.of(8, 0, 0))
                .closeTime(LocalTime.of(0, 0, 0))
                .holiday(false)
                .facility(getFacilityLoop(i))
                .build();

            hours.add(operatingHour);
        }

        return hours;
    }

    private List<FacilityManager> getFacilityManagers() {
        List<FacilityManager> facilityManagers = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            FacilityManager facilityManager = new FacilityManager(Long.valueOf(i), getFacilityLoop(i), getUserLoop(i));

            facilityManagers.add(facilityManager);
        }

        return facilityManagers;
    }

    private User getUserLoop(int idx) {
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

    private Facility getFacilityLoop(int idx) {
        Address address = Address.builder()
            .city("서울")
            .build();

        Facility facility = Facility.builder()
            .id(Long.valueOf(idx))
            .name("시설" + idx)
            .address(address)
            .sportType(SportType.FUTSAL)
            .conventient("1000")
            .build();
        return facility;
    }

}
