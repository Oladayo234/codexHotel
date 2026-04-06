package com.semicolon.codexHotel.data.models.enums;

public enum RoomType {
    SINGLE(10000),
    DOUBLE(15000),
    SUITE(25000);

    private final double pricePerNight;

    RoomType(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }
}
