package com.reservemate.reserve_mate_backend.facility.domain;

import com.reservemate.reserve_mate_backend.common.domain.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;

public class FacilityTest {

    @Test
    @DisplayName("Facility 객체 생성")
    void builder_Facility(){
        String name = "마포풋살";
        String description = "마포에 위치한 풋살장입니다.";
        Address address = Address.builder()
                        .zipcode("")
                        .city("서울특별시")
                         .district("마포구")
                         .streetAddress("마포로")
                         .detailAddress("1층")
                .build();
        String contactPhone = "01012341234";

        Facility facility = Facility.builder()
                .name(name)
                .description(description)
                .address(address)
                .contactPhone(contactPhone)
                .build();
        assertThat(facility.getName()).isEqualTo(name);
        assertThat(facility.getDescription()).isEqualTo(description);
        assertThat(facility.getAddress()).isEqualTo(address);
        assertThat(facility.getContactPhone()).isEqualTo(contactPhone);
    }
}
