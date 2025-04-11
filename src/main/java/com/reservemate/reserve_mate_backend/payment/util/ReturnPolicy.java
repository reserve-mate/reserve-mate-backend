package com.reservemate.reserve_mate_backend.payment.util;

public class ReturnPolicy {

    // 환급율
    public static final double RETURN_80 = 0.8;
    public static final double RETURN_20 = 0.2;

    // 환불 가능 시간 간격
    public static final int TWO_DAY_AGO_BY_HOUR = 48;
    public static final int ONE_DAY_AGO_BY_HOUR = 24;
    public static final int ONE_DAY_AGO_BY_MINUTE = 1440;
    public static final int ONE_HOUR_HALF_AGO = 90;

}
