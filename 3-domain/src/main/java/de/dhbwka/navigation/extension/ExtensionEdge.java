package de.dhbwka.navigation.extension;

import de.dhbwka.navigation.extension.filter.ExtensionEdgeCategory;
import de.dhbwka.navigation.graph.edge.CategorizedGeoEdge;
import de.dhbwka.navigation.graph.node.GeoNode;

public class ExtensionEdge<
        NodeType extends GeoNode
    > implements CategorizedGeoEdge<NodeType, ExtensionEdgeCategory> {

    private final NodeType origin;
    private final NodeType destination;

    private final ExtensionEdgeCategory category;

    public ExtensionEdge(NodeType origin, NodeType destination, ExtensionEdgeCategory category) {
        this.origin = origin;
        this.destination = destination;
        this.category = category;
    }

    private static final ExtensionEdgeCategory CATEGORY_DEFAULT = ExtensionEdgeCategory.EXTENSION_TO_EXTENSION_ALONG_STREET;

    public ExtensionEdge(NodeType origin, NodeType destination) {
        this(origin, destination, CATEGORY_DEFAULT);
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
    public ExtensionEdgeCategory getCategory() {
        return category;
    }
}
