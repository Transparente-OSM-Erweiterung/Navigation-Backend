package de.dhbwka.navigation;

public interface GeoNode extends Node {
    double getLatitude();
    double getLongitude();

    default Vec2 toVec2() {
        return new Vec2(getLatitude(), getLongitude());
    }
}
