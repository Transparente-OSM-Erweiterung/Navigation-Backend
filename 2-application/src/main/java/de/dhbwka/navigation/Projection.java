package de.dhbwka.navigation;

import static de.dhbwka.navigation.Constants.EARTH_RADIUS_METERS;

public class Projection {
    private static final double METERS_PER_DEGREE
            = (2 * Math.PI * EARTH_RADIUS_METERS / 360.0);

    public static Vec2 projectToLocal(Vec2 globalPos, Vec2 reference) {
        double cosLat = Math.cos(Math.toRadians(reference.x)); // lat
        return new Vec2(
                globalPos.x * METERS_PER_DEGREE, // lat
                globalPos.y * cosLat * METERS_PER_DEGREE // lon
        );
    }

    public static Vec2 unprojectToGlobal(Vec2 localPos, Vec2 reference) {
        double cosLat = Math.cos(Math.toRadians(reference.x)); // lat
        return new Vec2(
                localPos.x / METERS_PER_DEGREE, // lat
                (localPos.y / METERS_PER_DEGREE) / cosLat // lon
        );
    }
}
