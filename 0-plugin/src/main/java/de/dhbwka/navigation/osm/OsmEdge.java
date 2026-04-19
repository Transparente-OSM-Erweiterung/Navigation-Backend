package de.dhbwka.navigation.osm;

import de.dhbwka.navigation.*;


public class OsmEdge<NodeType extends GeoNode> implements CategorizedGeoEdge<NodeType, ExtensionEdgeCategory> {

    private final NodeType origin;
    private final NodeType destination;

    private final boolean directed;

    private final StreetCategory type;

    private final double width;

    public OsmEdge(
            NodeType origin,
            NodeType destination,
            boolean directed,
            StreetCategory type,
            double width
    ) {
        this.origin = origin;
        this.destination = destination;
        this.directed = directed;
        this.type = type;
        this.width = width;
    }

    private static final boolean DIRECTED_DEFAULT = true;
    private static final StreetCategory TYPE_DEFAULT = StreetCategory.STREET;

    public OsmEdge(
            NodeType origin,
            NodeType destination,
            boolean directed,
            double width
    ) {
        this(
                origin,
                destination,
                directed,
                TYPE_DEFAULT,
                width
        );
    }

    public OsmEdge(
            NodeType origin,
            NodeType destination,
            StreetCategory type,
            double width
    ) {
        this(
                origin,
                destination,
                DIRECTED_DEFAULT,
                type,
                width
        );
    }

    public OsmEdge(
            NodeType origin,
            NodeType destination,
            double width
    ) {
        this(
                origin,
                destination,
                DIRECTED_DEFAULT,
                TYPE_DEFAULT,
                width
        );
    }

    @Override
    public NodeType getOrigin() {
        return origin;
    }

    @Override
    public NodeType getDestination() {
        return destination;
    }

    @Override
    public boolean isDirected() {
        return directed;
    }

    public StreetCategory getType() {
        return type;
    }

    public double getWidth() {
        return width;
    }

    @Override
    public ExtensionEdgeCategory getCategory() {
        return ExtensionEdgeCategory.BASE_TO_BASE;
    }
}
