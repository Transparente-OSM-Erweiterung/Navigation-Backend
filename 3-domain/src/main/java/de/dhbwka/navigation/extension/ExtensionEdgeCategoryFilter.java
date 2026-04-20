package de.dhbwka.navigation.extension;

import de.dhbwka.navigation.graph.edge.CategorizedEdge;
import de.dhbwka.navigation.graph.Graph;
import de.dhbwka.navigation.graph.node.Node;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public class ExtensionEdgeCategoryFilter <
        NodeType extends Node,
        EdgeType extends CategorizedEdge<? extends NodeType, ExtensionEdgeCategory>
    > implements Graph<NodeType, EdgeType> {

    private final Graph<NodeType, EdgeType> graph;

    private final Predicate<EdgeType> filterPredicate;

    public ExtensionEdgeCategoryFilter(
            Graph<NodeType, EdgeType> graph,
            String routingStartNodeId,
            String routingDestinationNodeId
    ) {
        this.graph = graph;
        filterPredicate = new ExtensionEdgeCategoryFilterPredicate<>(routingStartNodeId, routingDestinationNodeId);
    }

    @Override
    public Optional<NodeType> getNode(String id) {
        return graph.getNode(id);
    }

    @Override
    public Collection<EdgeType> getEdgesFrom(String nodeId) {
        return graph.getEdgesFrom(nodeId)
                .stream()
                .filter(filterPredicate)
                .toList();
    }
}
