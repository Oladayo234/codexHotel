package com.semicolon.codexHotel.utils;

import com.semicolon.codexHotel.data.models.enums.RoomType;

public class GenerateRoomNumber {

    private static int singleCount = 100;
    private static int doubleCount = 200;
    private static int suiteCount = 300;

    public static String generateRoomNumber(RoomType roomType) {
        if (roomType == RoomType.SINGLE) {
            return "SN-" + singleCount++;
        } else if (roomType == RoomType.DOUBLE) {
            return "DB-" + doubleCount++;
        } else {
            return "ST-" + suiteCount++;
        }
    }
}
