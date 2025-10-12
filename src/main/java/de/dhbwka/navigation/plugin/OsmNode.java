package de.dhbwka.navigation.plugin;

import de.dhbwka.navigation.domain.GeoNode;

public class OsmNode implements GeoNode {

    private final String id;
    private final double latitude, longitude;

    public OsmNode(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }
}
