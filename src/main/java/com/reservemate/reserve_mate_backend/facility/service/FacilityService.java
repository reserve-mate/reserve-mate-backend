package com.reservemate.reserve_mate_backend.facility.service;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.facility.repository.OperationHourRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacilityService {

    private final FacilityManagerRepository facilityManagerRepository;
    private final OperationHourRepository hourRepository;

}
