package de.dhbwka.navigation;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public class ExtensionEdgeCategoryFilter <
        NodeType extends Node,
        EdgeType extends CategorizedEdge<? extends NodeType, ExtensionEdgeCategory>
    > implements Graph<NodeType, EdgeType> {

    private final Graph<NodeType, EdgeType> graph;
    private final Predicate<EdgeType> filterPredicate;

    @Deprecated
    private String routingStartNodeId;

    @Deprecated
    private String routingDestinationNodeId;

    public ExtensionEdgeCategoryFilter(
            Graph<NodeType, EdgeType> graph,
            String routingStartNodeId,
            String routingDestinationNodeId
    ) {
        this.graph = graph;
        filterPredicate = new ExtensionEdgeCategoryFilterPredicate<>(routingStartNodeId, routingDestinationNodeId);
        this.routingStartNodeId = routingStartNodeId;
        this.routingDestinationNodeId = routingDestinationNodeId;
    }

    @Override
    public Optional<NodeType> getNode(String id) {
        return graph.getNode(id);
    }

    @Deprecated
    @Override
    public Collection<NodeType> getNeighbors(NodeType from) {
        return graph.getNeighbors(from)
                .stream()
                .filter(to ->
                        from.getId().equals(routingStartNodeId)
                        || to.getId().equals(routingDestinationNodeId)
                        || to.getId().matches("[0-9]+[a-z]+")
                )
                .toList();
    }

    @Override
    public Collection<EdgeType> getEdgesFrom(String nodeId) {
        return graph.getEdgesFrom(nodeId)
                .stream()
                .filter(filterPredicate)
                .toList();
    }
}
