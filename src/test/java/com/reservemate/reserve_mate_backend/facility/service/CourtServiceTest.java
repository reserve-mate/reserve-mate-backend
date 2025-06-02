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
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.CourtType;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.facility.dto.response.CourtNamsResponseDto;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;

import jakarta.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
public class CourtServiceTest {

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private CourtRepository courtRepository;

    @InjectMocks
    private CourtService courtService;

    @Test
    @DisplayName("코트 이름 목록 조회 테스트")
    void testGetCourtNames() {
        /* given */
        Facility facility = getFacility();
        List<Court> court = getCourt(facility);

        given(facilityRepository.findById(facility.getId())).willReturn(Optional.of(facility));
        given(courtRepository.findByFacilityAndActive(facility, true)).willReturn(court);

        /* when */
        List<CourtNamsResponseDto> responseDtos = courtService.getCourtNames(facility.getId());

        /* then */
        assertThat(responseDtos.size()).isEqualTo(2);
        assertThat(responseDtos.get(0).getCourtName()).isEqualTo(court.get(0).getName());

    }

    private List<Court> getCourt(Facility facility) {
        List<Court> courts = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {

            Court court = new Court(Long.valueOf(i), "코트" + i, CourtType.ARTIFICIAL_TURF_FUTSAL, 20, 40, false, 0,
                facility);

            courts.add(court);
        }

        return courts;
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
