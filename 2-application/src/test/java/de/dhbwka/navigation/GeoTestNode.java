package de.dhbwka.navigation;

public record GeoTestNode(String id, double lat, double lon) implements GeoNode{

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

    public static class Factory implements ExtensionNodeFactory<GeoTestNode> {

        @Override
        public GeoTestNode createNode(String id) {
            return new GeoTestNode(id, 0, 0);
        }
    }
}
