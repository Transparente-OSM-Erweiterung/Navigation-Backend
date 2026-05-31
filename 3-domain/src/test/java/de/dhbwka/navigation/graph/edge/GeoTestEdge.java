package de.dhbwka.navigation.graph.edge;

import de.dhbwka.navigation.graph.node.GeoTestNode;
import de.dhbwka.navigation.extension.filter.ExtensionEdgeCategory;

public class GeoTestEdge implements CategorizedGeoEdge<GeoTestNode, ExtensionEdgeCategory>, WidthedEdge<GeoTestNode>, SideWalkClassified {
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
    public double getWidth() {
        return 1;
    }

    @Override
    public ExtensionEdgeCategory getCategory() {
        return ExtensionEdgeCategory.BASE_TO_BASE;
    }

    @Override
    public boolean hasSideWalkLeft() {
        return true;
    }

    @Override
    public boolean hasSideWalkRight() {
        return true;
    }
}
