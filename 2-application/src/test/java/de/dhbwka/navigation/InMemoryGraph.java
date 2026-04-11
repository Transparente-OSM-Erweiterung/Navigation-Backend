package de.dhbwka.navigation;

import java.util.*;

public class InMemoryGraph<NodeType extends Node, EdgeType extends Edge<NodeType>> implements GraphWithWidth<NodeType, EdgeType>, GraphBuilder<NodeType, EdgeType> {

    private final Map<String, NodeType> nodes = new HashMap<>();
    private final Map<String, List<NodeType>> adjacencyMap = new HashMap<>();

    private final Map<String, List<EdgeType>> edgeMap = new HashMap<>();

    @Override
    public Optional<NodeType> getNode(String id) {
        return Optional.ofNullable(nodes.get(id));
    }

    @Deprecated
    @Override
    public Collection<NodeType> getNeighbors(NodeType node) {
        return adjacencyMap.getOrDefault(node.getId(), List.of());
    }

    @Override
    public Collection<EdgeType> getEdgesFrom(NodeType node) {
        return edgeMap.get(node.getId());
    }

    @Override
    public void addNode(NodeType node) {
        nodes.putIfAbsent(node.getId(), node);
        adjacencyMap.computeIfAbsent(node.getId(), k -> new ArrayList<>());
        edgeMap.computeIfAbsent(node.getId(), k -> new ArrayList<>());
    }

    @Override
    public void addEdge(EdgeType edge) {
        edgeMap.get(edge.getOrigin().getId()).add(edge);
    }

    @Deprecated
    @Override
    public void addEdge(String fromId, String toId, boolean bidirectional, double streetWidth) {
        NodeType fromNode = nodes.get(fromId);
        NodeType toNode = nodes.get(toId);
        if (fromNode == null || toNode == null) {
            throw new IllegalArgumentException("Both nodes must exist before adding an edge: " + fromId + " => " + toId);
        }
        adjacencyMap.get(fromId).add(toNode);
        if(bidirectional){
            adjacencyMap.get(toId).add(fromNode);
        }
    }

    @Override
    public double getWidth(String fromId, String toId) {
        return 1;
    }
}
