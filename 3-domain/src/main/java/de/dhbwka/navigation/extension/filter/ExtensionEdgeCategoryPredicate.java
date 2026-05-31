package de.dhbwka.navigation.extension.filter;

import de.dhbwka.navigation.graph.edge.CategorizedEdge;
import de.dhbwka.navigation.graph.node.Node;

import java.util.function.Predicate;

public class ExtensionEdgeCategoryPredicate<NodeType extends Node, EdgeType extends CategorizedEdge<? extends NodeType, ExtensionEdgeCategory>> implements Predicate<EdgeType> {
    private final String  routingStartNodeId;
    private final String routingDestinationNodeId;

    public ExtensionEdgeCategoryPredicate(String routingStartNodeId, String routingDestinationNodeId) {
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
