package de.dhbwka.navigation.osm;

import de.dhbwka.navigation.extension.filter.ExtensionEdgeCategory;
import de.dhbwka.navigation.graph.edge.CategorizedWidthedSidewalkClassifiedGeoEdge;
import de.dhbwka.navigation.graph.edge.SideWalkClassified;
import de.dhbwka.navigation.graph.node.GeoNode;


public class OsmEdge<NodeType extends GeoNode> implements
        CategorizedWidthedSidewalkClassifiedGeoEdge<NodeType, ExtensionEdgeCategory>,
        SideWalkClassified
{

    private final NodeType origin;
    private final NodeType destination;


    private final StreetCategory streetCategory;

    private final double width;
    private final boolean sideWalkLeft;
    private final boolean sideWalkRight;

    public OsmEdge(
            NodeType origin,
            NodeType destination,
            StreetCategory streetCategory,
            double width,
            boolean sideWalkLeft,
            boolean sideWalkRight
    ) {
        this.origin = origin;
        this.destination = destination;
        this.streetCategory = streetCategory;
        this.width = width;
        this.sideWalkLeft = sideWalkLeft;
        this.sideWalkRight = sideWalkRight;
    }

    @Override
    public NodeType getOrigin() {
        return origin;
    }

    @Override
    public NodeType getDestination() {
        return destination;
    }

    public StreetCategory getStreetCategory() {
        return streetCategory;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public ExtensionEdgeCategory getCategory() {
        return ExtensionEdgeCategory.BASE_TO_BASE;
    }

    @Override
    public boolean hasSideWalkLeft() {
        return sideWalkLeft;
    }

    @Override
    public boolean hasSideWalkRight() {
        return sideWalkRight;
    }
}
