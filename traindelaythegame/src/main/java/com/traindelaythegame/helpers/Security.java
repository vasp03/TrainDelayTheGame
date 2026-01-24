package com.traindelaythegame.helpers;

public class Security {
    public static boolean isValidApiRequest(String query) {
        if (query == null || query.isEmpty()) return false;

        // Generic identifier: letters, numbers, spaces, underscore, dash
        return query.matches("^[a-zA-Z0-9 _\\-]+$");
    }

    public static boolean isValidNumericList(String query) {
        if (query == null || query.isEmpty()) return false;

        // Allow digits, decimal points, commas, semicolons, spaces and minus for negative numbers
        return query.matches("^[0-9.,;\\- ]+$");
    }

    public static boolean isValidTypeList(String query) {
        if (query == null || query.isEmpty()) return true; // optional

        // Comma separated words (letters only)
        return query.matches("^[a-zA-Z]+(,[a-zA-Z]+)*$");
    }
}
