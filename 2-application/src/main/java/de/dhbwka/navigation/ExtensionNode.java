package de.dhbwka.navigation;

public class ExtensionNode implements GeoNode{
    private double latitude;
    private double longitude;
    private final String id;

    public ExtensionNode(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "[id= "
                + id
                + ", lat="
                + latitude
                + ", lon="
                + longitude
                +"]";
    }
}
