package de.dhbwka.navigation;

public class  HaversineScorer <T extends GeoNode> implements Scorer<T> {

    @Override
    public double computeCost(T from, T to) {
        double r = 6372.8; // Earth's Radius, in kilometers

        double dLat = Math.toRadians(to.getLatitude() - from.getLatitude());
        double dLon = Math.toRadians(to.getLongitude() - from.getLongitude());
        double lat1 = Math.toRadians(from.getLatitude());
        double lat2 = Math.toRadians(to.getLatitude());

        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return r * c /* temp TODO */ /* * (from instanceof ExtensionNode && to instanceof ExtensionNode ? 0.1 : 1)*/;
    }
}
