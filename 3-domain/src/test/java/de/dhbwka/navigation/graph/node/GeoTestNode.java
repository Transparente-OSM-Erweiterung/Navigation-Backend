package de.dhbwka.navigation.graph.node;

public record GeoTestNode(String id, double lat, double lon) implements GeoNode {

    @Override
    public double getLatitude() {
        return lat;
    }

    @Override
    public double getLongitude() {
        return lon;
    }

    @Override
    public String getId() {
        return id;
    }
}
