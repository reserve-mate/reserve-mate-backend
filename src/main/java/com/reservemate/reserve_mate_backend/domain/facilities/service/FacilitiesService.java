package com.reservemate.reserve_mate_backend.domain.facilities.service;

import com.reservemate.reserve_mate_backend.domain.facilities.repository.FacilitiesRepository;
import org.springframework.stereotype.Service;

@Service
public class FacilitiesService {
    private final FacilitiesRepository facilitiesRepository;
    public FacilitiesService(FacilitiesRepository facilitiesRepository) {
        this.facilitiesRepository = facilitiesRepository;
    }
}
