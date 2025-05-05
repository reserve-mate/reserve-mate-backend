package com.reservemate.reserve_mate_backend.admin.facilities.dto;

import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import java.util.List;
import lombok.Getter;

@Getter
public class RequestCreateFacility {

    private String name;
    private SportType sportType;
    private Address address;
    private String detailAddress;
    private String description;

    private boolean hasParking;
    private boolean hasShower;
    private boolean hasEquipmentRental;
    private boolean hasCafe;

    private List<RequestCreateCourt> courts;

    private List<RequestOperatingHour> operatingHours;

}
