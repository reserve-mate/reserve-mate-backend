package com.reservemate.reserve_mate_backend.facilities.service;

import com.reservemate.reserve_mate_backend.facilities.repository.FacilitiesRepository;
import org.springframework.stereotype.Service;

@Service
public class FacilitiesService {
  private final FacilitiesRepository facilitiesRepository;

  public FacilitiesService(FacilitiesRepository facilitiesRepository) {
    this.facilitiesRepository = facilitiesRepository;
  }
}
