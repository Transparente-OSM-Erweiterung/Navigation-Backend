package de.dhbwka.navigation;

import java.util.*;

public class InMemoryGraph<NodeType extends Node, EdgeType extends Edge<? extends NodeType>> implements Graph<NodeType, EdgeType>, GraphBuilder<NodeType, EdgeType> {

    private final Map<String, NodeType> nodes = new HashMap<>();

    private final Map<String, List<EdgeType>> edgeMap = new HashMap<>();

    @Override
    public Optional<NodeType> getNode(String id) {
        return Optional.ofNullable(nodes.get(id));
    }

    @Override
    public Collection<EdgeType> getEdgesFrom(String  nodeId) {
        return edgeMap.get(nodeId);
    }

    @Override
    public void addNode(NodeType node) {
        nodes.putIfAbsent(node.getId(), node);
        edgeMap.computeIfAbsent(node.getId(), k -> new ArrayList<>());
    }

    @Override
    public void addEdge(EdgeType edge) {
        edgeMap.get(edge.getOrigin().getId()).add(edge);
    }
}
