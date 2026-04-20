package de.dhbwka.navigation;

import java.util.function.Predicate;

public class ExtensionEdgeCategoryFilterPredicate<NodeType extends Node, EdgeType extends CategorizedEdge<? extends NodeType, ExtensionEdgeCategory>> implements Predicate<EdgeType> {
    private final String  routingStartNodeId;
    private final String routingDestinationNodeId;

    public ExtensionEdgeCategoryFilterPredicate(String routingStartNodeId, String routingDestinationNodeId) {
        this.routingStartNodeId = routingStartNodeId;
        this.routingDestinationNodeId = routingDestinationNodeId;
    }

    @Override
    public boolean test(EdgeType edge) {
        return edge.getOrigin().getId().equals(routingStartNodeId)
                || edge.getDestination().getId().equals(routingDestinationNodeId)
                || edge.getCategory().equals(ExtensionEdgeCategory.EXTENSION_TO_EXTENSION_ALONG_STREET)
                || edge.getCategory().equals(ExtensionEdgeCategory.EXTENSION_TO_EXTENSION_CROSSING_STREET);
    }
}
