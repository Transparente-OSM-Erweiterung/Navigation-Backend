package de.dhbwka.navigation.graph;

import de.dhbwka.navigation.graph.edge.Edge;
import de.dhbwka.navigation.graph.node.Node;

import java.util.*;

public class InMemoryGraph<NodeType extends Node, EdgeType extends Edge<? extends NodeType>> implements Graph<NodeType, EdgeType>, GraphBuilder<NodeType, EdgeType> {

    private final Map<String, NodeType> nodes = new HashMap<>();

    private final Map<String, List<EdgeType>> edges = new HashMap<>();

    @Override
    public Optional<NodeType> getNode(String id) {
        return Optional.ofNullable(nodes.get(id));
    }

    @Override
    public Collection<EdgeType> getEdgesFrom(String  nodeId) {
        return edges.getOrDefault(nodeId, Collections.emptyList());
    }

    @Override
    public void addNode(NodeType node) {
        nodes.put(node.getId(), node);
    }

    @Override
    public void addEdge(EdgeType edge) {
        edges.computeIfAbsent(
                edge.getOrigin().getId(),
                id -> new ArrayList<>()
        ).add(edge);
    }
}
