package de.dhbwka.navigation;

public class GeoTestEdge implements CategorizedGeoEdge<GeoTestNode, ExtensionEdgeCategory>, WidthedEdge<GeoTestNode> {
    final GeoTestNode origin;
    final GeoTestNode destination;

    public GeoTestEdge(GeoTestNode origin, GeoTestNode destination) {
        this.origin = origin;
        this.destination = destination;
    }

    @Override
    public GeoTestNode getOrigin() {
        return origin;
    }

    @Override
    public GeoTestNode getDestination() {
        return destination;
    }

    @Override
    public boolean isDirected() {
        return false;
    }

    @Override
    public double getWidth() {
        return 1;
    }

    @Override
    public ExtensionEdgeCategory getCategory() {
        return ExtensionEdgeCategory.BASE_TO_BASE;
    }
}
