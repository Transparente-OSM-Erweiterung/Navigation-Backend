package de.dhbwka.navigation;

import java.util.*;

public class InMemoryGraph<NodeType extends Node, EdgeType extends Edge<? extends NodeType>> implements Graph<NodeType, EdgeType>, GraphBuilder<NodeType, EdgeType> {

    private final Map<String, NodeType> nodes = new HashMap<>();

    @Deprecated
    private final Map<String, List<NodeWithWidth<NodeType>>> adjacencyMap = new HashMap<>();

    private final Map<String, List<EdgeType>> edgeMap = new HashMap<>();

    @Override
    public Optional<NodeType> getNode(String id) {
        Objects.requireNonNull(id);
        return Optional.ofNullable(nodes.get(id));
    }

    @Deprecated
    @Override
    public Collection<NodeType> getNeighbors(NodeType node) {
        Objects.requireNonNull(node);
        if(adjacencyMap.get(node.getId()) == null) return List.of();
        return adjacencyMap.get(node.getId()).stream().map(nww -> nww.node).toList();
    }

    @Override
    public Collection<EdgeType> getEdgesFrom(String  nodeId) {
        return edgeMap.get(nodeId);
    }

    @Override
    public void addNode(NodeType node) {
        Objects.requireNonNull(node);
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
    public void addEdge(String fromId, String toId, boolean bidirectional, double streetwidth) {
        Objects.requireNonNull(fromId);
        Objects.requireNonNull(toId);
        NodeType fromNode = nodes.get(fromId);
        NodeType toNode = nodes.get(toId);
        if (fromNode == null || toNode == null) {
            throw new IllegalArgumentException("Both nodes must exist before adding an edge: " + fromId + " => " + toId);
        }

        adjacencyMap.get(fromId).add(new NodeWithWidth<>(toNode, streetwidth));
        if(bidirectional){
            adjacencyMap.get(toId).add(new NodeWithWidth<>(fromNode, streetwidth));
        }
    }

    private record NodeWithWidth<T extends Node> (T node, Double width){}
}
