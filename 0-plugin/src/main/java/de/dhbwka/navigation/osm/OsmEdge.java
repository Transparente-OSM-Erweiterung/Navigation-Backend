package de.dhbwka.navigation.osm;

import de.dhbwka.navigation.*;


public class OsmEdge<NodeType extends GeoNode> implements
        CategorizedGeoEdge<NodeType, ExtensionEdgeCategory>,
        WidthedEdge<NodeType>
{

    private final NodeType origin;
    private final NodeType destination;


    private final StreetCategory streetCategory;

    private final double width;

    public OsmEdge(
            NodeType origin,
            NodeType destination,
            StreetCategory streetCategory,
            double width
    ) {
        this.origin = origin;
        this.destination = destination;
        this.streetCategory = streetCategory;
        this.width = width;
    }

    private static final StreetCategory TYPE_DEFAULT = StreetCategory.STREET;

    public OsmEdge(
            NodeType origin,
            NodeType destination,
            double width
    ) {
        this(
                origin,
                destination,
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
}
