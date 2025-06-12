package com.reservemate.reserve_mate_backend.admin.facilities.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.common.file.service.FileService;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.domain.ManagerRole;
import com.reservemate.reserve_mate_backend.facility.domain.OperatingHour;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.facility.dto.response.FacilityNameResponseDto;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityImageRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;
import com.reservemate.reserve_mate_backend.facility.repository.OperationHourRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.domain.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AdminFacilityServiceTest {

    @Mock
    private FacilityManagerRepository facilityManagerRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private OperationHourRepository operationHourRepository;

    @Mock
    private FileService fileService;

    @Mock
    private FacilityImageRepository facilityImageRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AdminFacilityService adminFacilityService;

    @Test
    @DisplayName("매치 등록 시 시설 조회")
    void testGetMatchFacilityNames() {
        /* given */
        List<FacilityManager> facilityManagers = getFacilityManagers();
        List<OperatingHour> hours = getOperationHours(facilityManagers);

        HttpServletRequest request = mock(HttpServletRequest.class);
        String mockAccessToken = "mock.access.token";

        given(request.getHeader("access")).willReturn(mockAccessToken);
        given(jwtUtil.getId(mockAccessToken)).willReturn(1L);
        given(facilityManagerRepository.findByUserId(1L)).willReturn(facilityManagers);
        given(operationHourRepository.findByFacilityInAndDayOfWeek(FacilityManager.getFacilityIds(facilityManagers),
            Utils.getDayOfWeek()))
            .willReturn(hours);

        /* when */
        List<FacilityNameResponseDto> responseDtos = adminFacilityService.getMatchFacilityNames(request,
            SportType.FUTSAL);

        /* then */
        assertThat(responseDtos.size()).isEqualTo(2);
        assertThat(responseDtos.get(0).getFacilityName()).isEqualTo("시설1");
    }

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
            FacilityManager facilityManager = new FacilityManager(Long.valueOf(i), getFacilityLoop(i), getUserLoop(i),
                ManagerRole.MANAGER);

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
