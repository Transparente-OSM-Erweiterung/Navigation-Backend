package de.dhbwka.navigation;

import static de.dhbwka.navigation.Constants.EARTH_RADIUS_METERS;

public abstract class Haversine {
    public static double distance(Vec2 from, Vec2 to) {
        double dLat = Math.toRadians(to.x - from.x); // x is lat, y is lon
        double dLon = Math.toRadians(to.y - from.y);
        double lat1 = Math.toRadians(from.x);
        double lat2 = Math.toRadians(to.x);

        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return EARTH_RADIUS_METERS * c;
    }
}
