package de.dhbwka.navigation;

public class ExtensionEdge<NodeType extends GeoNode> implements GeoEdge<NodeType> {

    private final NodeType origin;
    private final NodeType destination;

    private final boolean directed;
    private final ExtensionEdgeType type;

    public ExtensionEdge(NodeType origin, NodeType destination, boolean directed, ExtensionEdgeType type) {
        this.origin = origin;
        this.destination = destination;
        this.directed = directed;
        this.type = type;
    }

    private static final ExtensionEdgeType TYPE_DEFAULT = ExtensionEdgeType.EXTENSION_TO_EXTENSION_ALONG_STREET;
    private static final boolean DIRECTED_DEFAULT = true;

    public ExtensionEdge(NodeType origin, NodeType destination, boolean directed) {
        this(origin, destination, directed, TYPE_DEFAULT);
    }

    public ExtensionEdge(NodeType origin, NodeType destination, ExtensionEdgeType type) {
        this(origin, destination, DIRECTED_DEFAULT, type);
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

    public ExtensionEdgeType getType() {
        return type;
    }

    @Override
    public boolean isDirected() {
        return directed;
    }
}
