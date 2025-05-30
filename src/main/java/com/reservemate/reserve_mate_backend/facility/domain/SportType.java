package com.reservemate.reserve_mate_backend.facility.domain;

public enum SportType {

    ALL("A"), SOCCER("S"), BASKETBALL("BB"), TENNIS("T"), BADMINTON("BD"), BASEBALL("B"), FUTSAL("F");

    private final String prefix;

    SportType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }
}
