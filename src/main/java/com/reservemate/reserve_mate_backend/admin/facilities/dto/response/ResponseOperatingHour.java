package com.reservemate.reserve_mate_backend.admin.facilities.dto.response;

import com.reservemate.reserve_mate_backend.facility.domain.OperatingHour;
import java.time.DayOfWeek;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseOperatingHour {

    private DayOfWeek dayOfWeek;
    private String openTime;
    private String closeTime;
    private Boolean holiday;

    @Builder
    public ResponseOperatingHour(DayOfWeek dayOfWeek, String openTime, String closeTime,
        Boolean holiday) {
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.holiday = holiday;
    }

    public static ResponseOperatingHour getOperatingHour(OperatingHour entity) {
        return ResponseOperatingHour.builder()
            .dayOfWeek(entity.getDayOfWeek())
            .openTime(entity.getOpenTime().toString())
            .closeTime(entity.getCloseTime().toString())
            .holiday(entity.isHoliday())
            .build();
    }
}
