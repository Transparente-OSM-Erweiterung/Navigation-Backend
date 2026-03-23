package de.dhbwka.navigation;

import java.util.*;

public class InMemoryGraph<T extends Node> implements GraphWithWidth<T>, GraphBuilder<T> {

    private final Map<String, T> nodes = new HashMap<>();
    private final Map<String, List<NodeWithWidth<T>>> adjacencyMap = new HashMap<>();

    @Override
    public Optional<T> getNode(String id) {
        Objects.requireNonNull(id);
        return Optional.ofNullable(nodes.get(id));
    }

    @Override
    public Collection<T> getNeighbors(T node) {
        Objects.requireNonNull(node);
        return adjacencyMap.get(node.getId()).stream().map(nww -> nww.node).toList();
    }

    @Override
    public void addNode(T node) {
        Objects.requireNonNull(node);
        nodes.putIfAbsent(node.getId(), node);
        adjacencyMap.computeIfAbsent(node.getId(), k -> new ArrayList<>());
    }

    @Override
    public void addEdge(String fromId, String toId, boolean bidirectional, double streetwidth) {
        Objects.requireNonNull(fromId);
        Objects.requireNonNull(toId);
        T fromNode = nodes.get(fromId);
        T toNode = nodes.get(toId);
        if (fromNode == null || toNode == null) {
            throw new IllegalArgumentException("Both nodes must exist before adding an edge: " + fromId + " => " + toId);
        }

        adjacencyMap.get(fromId).add(new NodeWithWidth<>(toNode, streetwidth));
        if(bidirectional){
            adjacencyMap.get(toId).add(new NodeWithWidth<>(fromNode, streetwidth));
        }
    }

    @Override
    public double getWidth(String fromId, String toId) {
        return adjacencyMap.get(fromId).stream().filter(nww -> nww.node.getId().equals(toId)).findFirst().orElseThrow().width;
    }

    private record NodeWithWidth<T extends Node> (T node, Double width){}
}
