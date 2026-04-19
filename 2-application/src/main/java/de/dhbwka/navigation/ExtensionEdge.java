package de.dhbwka.navigation;

public class ExtensionEdge<
        NodeType extends GeoNode
    > implements CategorizedGeoEdge<NodeType, ExtensionEdgeCategory> {

    private final NodeType origin;
    private final NodeType destination;

    private final boolean directed;
    private final ExtensionEdgeCategory category;

    public ExtensionEdge(NodeType origin, NodeType destination, boolean directed, ExtensionEdgeCategory category) {
        this.origin = origin;
        this.destination = destination;
        this.directed = directed;
        this.category = category;
    }

    private static final ExtensionEdgeCategory TYPE_DEFAULT = ExtensionEdgeCategory.EXTENSION_TO_EXTENSION_ALONG_STREET;
    private static final boolean DIRECTED_DEFAULT = true;

    public ExtensionEdge(NodeType origin, NodeType destination, boolean directed) {
        this(origin, destination, directed, TYPE_DEFAULT);
    }

    public ExtensionEdge(NodeType origin, NodeType destination, ExtensionEdgeCategory category) {
        this(origin, destination, DIRECTED_DEFAULT, category);
    }

    public ExtensionEdge(NodeType origin, NodeType destination) {
        this(origin, destination, DIRECTED_DEFAULT, TYPE_DEFAULT);
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

    @Override
    public ExtensionEdgeCategory getCategory() {
        return category;
    }
}
