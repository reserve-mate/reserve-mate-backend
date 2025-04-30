package com.reservemate.reserve_mate_backend.admin.facilities.dto;

import java.time.DayOfWeek;
import lombok.Getter;

@Getter
public class RequestOperatingHour {
  private DayOfWeek dayOfWeek;
  private String openTime;
  private String closeTime;
  private Boolean holiday;
}
