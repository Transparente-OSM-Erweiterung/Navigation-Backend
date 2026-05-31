package de.dhbwka.navigation.extension.filter;

import de.dhbwka.navigation.graph.Graph;
import de.dhbwka.navigation.graph.edge.Edge;
import de.dhbwka.navigation.graph.node.Node;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public class FilteredGraph<NodeType extends Node, EdgeType extends Edge<? extends NodeType>> implements Graph<NodeType, EdgeType> {
    private final Graph<NodeType, EdgeType> graph;
    private final Predicate<EdgeType> criterion;

    public FilteredGraph(Graph<NodeType, EdgeType> graph, Predicate<EdgeType> criterion) {
        this.graph = graph;
        this.criterion = criterion;
    }

    @Override
    public Optional<NodeType> getNode(String id) {
        return graph.getNode(id);
    }

    @Override
    public Collection<EdgeType> getEdgesFrom(String nodeId) {
        return graph.getEdgesFrom(nodeId)
                .stream()
                .filter(criterion)
                .toList();
    }
}
