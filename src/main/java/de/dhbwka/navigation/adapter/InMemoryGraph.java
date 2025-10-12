package de.dhbwka.navigation.adapter;

import de.dhbwka.navigation.abstraction.Graph;
import de.dhbwka.navigation.abstraction.Node;

import java.util.*;

public class InMemoryGraph<T extends Node> implements Graph<T>, GraphBuilder<T> {

    private final Map<String, T> nodes = new HashMap<>();
    private final Map<String, List<T>> adjacencyMap = new HashMap<>();

    @Override
    public Optional<T> getNode(String id) {
        return Optional.ofNullable(nodes.get(id));
    }

    @Override
    public Collection<T> getNeighbors(T node) {
        return adjacencyMap.getOrDefault(node.getId(), List.of());
    }

    @Override
    public void addNode(T node) {
        nodes.putIfAbsent(node.getId(), node);
        adjacencyMap.computeIfAbsent(node.getId(), k -> new ArrayList<>());
    }

    @Override
    public void addEdge(String fromId, String toId, boolean bidirectional) {
        T fromNode = nodes.get(fromId);
        T toNode = nodes.get(toId);
        if (fromNode == null || toNode == null) {
            throw new IllegalArgumentException("Both nodes must exist before adding an edge: " + fromId + " => " + toId);
        }
        adjacencyMap.get(fromId).add(toNode);
        if(bidirectional){
            adjacencyMap.get(toId).add(fromNode);
        }
    }
}
